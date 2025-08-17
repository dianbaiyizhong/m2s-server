package com.nntk.m2s.service.impl;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Strings;
import com.nntk.m2s.constant.AreaLevelType;
import com.nntk.m2s.constant.CommonConst;
import com.nntk.m2s.mp.generate.entity.*;
import com.nntk.m2s.mp.generate.mapper.*;
import com.nntk.m2s.pojo.bo.MediaImage;
import com.nntk.m2s.pojo.bo.SinaNewsBo;
import com.nntk.m2s.repository.HttpRepository;
import com.nntk.m2s.service.IAiService;
import com.nntk.m2s.service.ISpiderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SpiderServiceImpl implements ISpiderService {
    @Resource
    private TProvinceMapper provinceMapper;

    @Resource
    private TCountryMapper countryMapper;

    @Resource
    private TCityMapper cityMapper;


    @Resource
    private TDistinctMapper distinctMapper;

    @Resource
    private IAiService aiService;

    private Map<Integer, String> provinceMap = new HashMap<>();
    private Map<Integer, String> cityMap = new HashMap<>();
    private Map<Integer, String> countryMap = new HashMap<>();

    private Map<Integer, String> distinctMap = new HashMap<>();


    @Resource
    private TNewsMapper newsMapper;


    private void loadGeoData() {
        if (provinceMap.isEmpty()) {
            List<TProvince> provinceList = provinceMapper.selectList(null);
            List<TCity> cityList = cityMapper.selectList(null);
            List<TCountry> countryList = countryMapper.selectList(null);
            List<TDistinct> distinctList = distinctMapper.selectList(null);

            provinceList.stream().forEach(item -> {
                provinceMap.put(item.getId(), item.getName());
            });
            cityList.stream().forEach(item -> {
                cityMap.put(item.getId(), item.getName());
            });
            countryList.stream().forEach(item -> {
                countryMap.put(item.getId(), item.getName());
            });
            distinctList.stream().forEach(item -> {
                distinctMap.put(item.getId(), item.getName());
            });

        }
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void spiderChinaNews() {

        loadGeoData();

        for (int i = 1; i <= 130; i++) {
            Map<String, Object> paramMap = new LinkedHashMap<>();
            paramMap.put("pageid", "121");
            paramMap.put("lid", "1356");
            paramMap.put("num", "20");
            paramMap.put("versionNumber", "1.2.4");
            paramMap.put("page", i);
            paramMap.put("encode", "utf-8");
            paramMap.put("callback", "feedCardJsonpCallback");
            paramMap.put("_", "1751176001407");
            String url = "https://feed.sina.com.cn/api/roll/get?" + URLUtil.buildQuery(paramMap, Charset.defaultCharset());

            log.info("开始爬取第{}页,{}", i, url);

            String html = HttpUtil.createGet(url).execute().body().replaceAll("try\\{feedCardJsonpCallback\\(", "").replaceAll("\\);}catch\\(e\\)\\{};", "");
            JSONObject jsonObject = JSON.parseObject(html);
            JSONObject result = jsonObject.getJSONObject("result");
            if (result != null) {
                int code = result.getJSONObject("status").getInteger("code");
                if (code != 0) {
                    throw new RuntimeException("url解析失败");
                }
                JSONArray data = result.getJSONArray("data");
                for (int j = 0; j < data.size(); j++) {
                    JSONObject item = data.getJSONObject(j);
                    SinaNewsBo newsEntity = new SinaNewsBo();
                    newsEntity = JSON.parseObject(item.toJSONString(), SinaNewsBo.class);
                    List<String> sources = JSON.parseArray(newsEntity.getUrls(), String.class);
                    if (!sources.isEmpty()) {
                        Map<String, String> map = new HashMap<>();
                        map.put("title", newsEntity.getTitle());
                        map.put("url", sources.get(0));
                        newsEntity.setNewsTime(getDateTimeOfTimestamp(Long.parseLong(Strings.padEnd(newsEntity.getIntime().toString(), 13, '0'))));
                        newsEntity.setSourceUrl(sources.get(0));

                        String title = newsEntity.getTitle();
                        boolean exists = newsMapper.exists(new QueryWrapper<TNews>().lambda()
                                .eq(TNews::getTitle, title)
                        );
                        if (exists) {
                            log.info("新闻标题已存在，跳过：{}", title);
                            continue;
                        }
                        List<MediaImage> mediaImage = JSON.parseArray(newsEntity.getSinaRawImages(), MediaImage.class);
                        if (!CollectionUtils.isEmpty(mediaImage)) {
                            List<String> imagesUrls = new ArrayList<>();
                            imagesUrls = mediaImage.stream().map(MediaImage::getU).toList();
                            newsEntity.setThumbUrl(imagesUrls.get(0));
                            newsEntity.setImages(JSON.toJSONString(newsEntity.getImages()));
                        }

                        try {
                            parseDetail(newsEntity);

                            insertNews2Db(newsEntity);
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.error("解析异常:{}:{}", title, e.getMessage());
                        }


                    }
                }
            }

        }


    }


    private void insertNews2Db(SinaNewsBo newsEntity) {
        if (newsEntity.getAreaLevel() == 0) {
            log.warn("由于没找到地区性关键词，该条新闻最终未入库:{}", newsEntity.getTitle());
            return;
        }

        TNews news = new TNews();
        news.setTitle(newsEntity.getTitle());
        news.setNewsContent(newsEntity.getRawContent());
        news.setNewsTime(newsEntity.getNewsTime());
        news.setCreateTime(LocalDateTime.now());
        news.setAreaLevel(newsEntity.getAreaLevel());
        news.setAreaId(newsEntity.getPosInfoId());
        news.setThumbImg(newsEntity.getThumbUrl());
        news.setSourceName(newsEntity.getMediaName());
        news.setSourceUrl(newsEntity.getSourceUrl());
        news.setImages(newsEntity.getImages());
        news.setNewsType(newsEntity.getNewsType());
        news.setComboId(0);
        news.setLocationSubtitle(newsEntity.getLocationSub());
        news.setMapNews(false);
        try {
            newsMapper.insert(news);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    @Override
    public void spiderI18nNews() {

        loadGeoData();

        String url = "https://news.sina.com.cn/world/";

        try {
            // 发送HTTP请求并获取文档对象
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(10000)
                    .get();

            // 选择新闻条目（根据实际网页结构调整选择器）
            Elements newsElements = doc.select(".news-item");

            for (Element element : newsElements) {
                // 提取标题
                String title = element.select(".news-item h2").text();

                String sourceUrl = element.select(".news-item a").attr("href");
                if (!sourceUrl.startsWith("http")) {
                    continue;
                }
                SinaNewsBo newsEntity = new SinaNewsBo();
                newsEntity.setTitle(title);

                boolean exists = newsMapper.exists(new QueryWrapper<TNews>().lambda()
                        .eq(TNews::getTitle, title)
                );
                if (exists) {
                    log.warn("新闻标题已存在，跳过：{}", title);
                    continue;
                }


                newsEntity.setSourceUrl(sourceUrl);
                newsEntity.setType(1);

                try {
                    parseDetail(newsEntity);
                    insertNews2Db(newsEntity);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("解析异常:{}:{}", title, e.getMessage());
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static LocalDateTime getDateTimeOfTimestamp(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }


    @Autowired
    private HttpRepository httpRepository;

    private void parseDetail(SinaNewsBo sinaNewsBo) {
        String body = httpRepository.get(sinaNewsBo.getSourceUrl());

        // 正则表达式截取来源：潇湘晨报<
        String sourceRegex = ">　　来源：(.*?)<";
        String source = ReUtil.get(sourceRegex, body, 1);
        if (StringUtils.isEmpty(source)) {
            sourceRegex = "<meta name=\"mediaid\" content=\"(.*?)\">";
            source = ReUtil.get(sourceRegex, body, 1);
        }
        sinaNewsBo.setMediaName(source);
        Document htmlBody = Jsoup.parse(body);
        // 解析所有图片
        if (StringUtils.isEmpty(sinaNewsBo.getThumbUrl())) {
            Elements imgElements = htmlBody.select(".article img");
            List<String> imagesUrls = new ArrayList<>();
            for (Element imgElement : imgElements) {
                String src = imgElement.attr("src");
                if (!src.startsWith("http")) {
                    src = "https:" + src;
                }
                imagesUrls.add(src);
            }
            if (!imagesUrls.isEmpty()) {
                sinaNewsBo.setThumbUrl(imagesUrls.get(0));
                sinaNewsBo.setImages(JSON.toJSONString(imagesUrls));
            }

            // 解析时间
            String adjustedDateTime = htmlBody.select("span.date").get(0).text();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年M月d日 HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(adjustedDateTime, formatter);
            sinaNewsBo.setNewsTime(dateTime);

        }

        // 解析原文章
        htmlBody.select("script").remove();
        String text = htmlBody.select(".article").html();
        sinaNewsBo.setRawContent(text.replaceAll("src=\"//k.sinaimg.cn", "src=\"https://k.sinaimg.cn"));
        sinaNewsBo.setContent(htmlBody.select(".article").text());
        String title = sinaNewsBo.getTitle();

        String prompt = """
                %s
                """.formatted(title + " " + sinaNewsBo.getContent());
        String deepSeekResponse = aiService.getBailianResponse(prompt);

        JSONObject aiBody = null;
        int newsType = 0;
        try {
            aiBody = JSON.parseObject(deepSeekResponse);
            newsType = aiBody.getInteger("type");
        } catch (Exception e) {
            log.error("ai解析异常:{}", deepSeekResponse);
            return;
        }

        if (!(newsType == 1 || newsType == 2 || newsType == 3 || newsType == 4 || newsType == 5 || newsType == 6 || newsType == 7 || newsType == 8)) {
            log.warn("新闻分类异常。【{}】", sinaNewsBo.getTitle());
            // matchText(sinaNewsBo);
        } else {
            log.info("title:{},aiBody:{}", title, aiBody);
            sinaNewsBo.setNewsType(newsType);

            String area = aiBody.getString("area");
            if (StringUtils.isEmpty(area)) {
                log.info("新闻解析不到地名:{},", title);
                return;
            }
            String detailAreaInfo = aiService.getBailianResponse(area, "b7350c95f9e04e5c9324cb6fe7c520eb");
            log.info("title:{},detailAreaInfo:{}", title, detailAreaInfo);
            JSONObject detailAreaInfoBody = JSON.parseObject(detailAreaInfo);

            // 判断是否含有地名关键字
            String city = detailAreaInfoBody.getString("city_name");
            String province = detailAreaInfoBody.getString("province_name");
            String country = detailAreaInfoBody.getString("country_name");
            String distinct = detailAreaInfoBody.getString("distinct_name");

            if (province.equals(city)) {
                // 解决北京市省份和北京市城市的问题
                city = null;
            }


            if (StringUtils.isNotEmpty(country)) {
                Integer countryKey = getLikeByMap(countryMap, country);
                if (countryKey != null) {
                    sinaNewsBo.setAreaLevel(4);
                    sinaNewsBo.setPosInfoId(countryKey);
                }
            }
            if (StringUtils.isNotEmpty(province)) {
                Integer provinceKey = getLikeByMap(provinceMap, province);
                if (provinceKey != null) {
                    sinaNewsBo.setAreaLevel(1);
                    sinaNewsBo.setPosInfoId(provinceKey);
                }
            }
            if (StringUtils.isNotEmpty(city)) {
                Integer cityKey = getLikeByMap(cityMap, city);
                if (cityKey != null) {
                    sinaNewsBo.setAreaLevel(2);
                    sinaNewsBo.setPosInfoId(cityKey);
                }
            }


            List<String> locationSubList = new ArrayList<>();
            if (sinaNewsBo.getAreaLevel() == AreaLevelType.COUNTRY.getCode()) {
                // 如果是国家，则只要记录
                if (StringUtils.isNotEmpty(province)) {
                    locationSubList.add(province);
                }
                if (StringUtils.isNotEmpty(city)) {
                    locationSubList.add(city);
                }
                if (StringUtils.isNotEmpty(distinct)) {
                    locationSubList.add(distinct);
                }
            }

            if (sinaNewsBo.getAreaLevel() == AreaLevelType.PROVINCE.getCode()) {
                if (StringUtils.isNotEmpty(city)) {
                    locationSubList.add(city);
                }
                if (StringUtils.isNotEmpty(distinct)) {
                    locationSubList.add(distinct);
                }
            }

            if (sinaNewsBo.getAreaLevel() == AreaLevelType.CITY.getCode()) {
                if (StringUtils.isNotEmpty(distinct)) {
                    locationSubList.add(distinct);
                }
            }

            if (!locationSubList.isEmpty()) {
                String locationSub = String.join("-", locationSubList);
                sinaNewsBo.setLocationSub(locationSub);
            }

            if (sinaNewsBo.getAreaLevel() == 0) {
                log.info("ai返回的地名没有识别到，那就使用文本判断:{}", title);
                matchText(sinaNewsBo);
            }
        }


    }

    private String buildNewsContentPrompt(String title) {
        String prompt = """
                %s。这是一个新闻，请返回那种比较详细的新闻概要，要求markdown格式，不需要图片内容。如果你暂时找不到相关新闻，可以返回“%s”关键字，让我方便识别
                """.formatted(title, CommonConst.NEWS_NOT_FOUND);
        return prompt;
    }

    private void matchText(SinaNewsBo sinaNewsBo) {
        boolean matchContent = judgeByStr(sinaNewsBo, sinaNewsBo.getRawContent());
        if (!matchContent) {
            log.info("正文没有匹配，继续用标题匹配");
            boolean matchTitle = judgeByStr(sinaNewsBo, sinaNewsBo.getTitle());
            if (matchTitle) {
                log.info("标题匹配成功:{}", sinaNewsBo.getAreaLevel() + ":" + sinaNewsBo.getPosInfoId());
            }
        }
    }


    private boolean judgeByStr(SinaNewsBo sinaNewsBo, String judgeContent) {
        Integer distinctKey = getLikeByMap(distinctMap, judgeContent);
        if (distinctKey != null) {
            sinaNewsBo.setAreaLevel(2);
            // 找到乡镇，那就直接找对应城市即可
            TDistinct distinct = distinctMapper.selectById(distinctKey);
            TCity city = cityMapper.selectOne(new QueryWrapper<TCity>().lambda()
                    .eq(TCity::getAreaCode, distinct.getAdCode())
            );
            if (city == null) {
                log.error("没匹配出城市:{}", distinct.getName());
            } else {
                log.info("通过乡镇级别地名{},识别到城市：{}", distinct.getName(), city.getName());
                sinaNewsBo.setPosInfoId(city.getId());
                return true;
            }
        }

        Integer cityKey = getLikeByMap(cityMap, judgeContent);
        if (cityKey != null) {
            sinaNewsBo.setAreaLevel(2);
            sinaNewsBo.setPosInfoId(cityKey);
            return true;
        }
        Integer provinceKey = getLikeByMap(provinceMap, judgeContent);
        if (provinceKey != null) {
            sinaNewsBo.setAreaLevel(1);
            sinaNewsBo.setPosInfoId(provinceKey);
            return true;
        }
        Integer countryKey = getLikeByMap(countryMap, judgeContent);
        if (countryKey != null) {
            sinaNewsBo.setAreaLevel(4);
            sinaNewsBo.setPosInfoId(countryKey);
            return true;
        }
        return false;
    }


    private static Integer getLikeByMap(Map<Integer, String> map, String keyLike) {

        for (Map.Entry<Integer, String> entity : map.entrySet()) {

            if (keyLike.equals(entity.getValue())) {
                return entity.getKey();
            }
        }
        return null;
    }


}
