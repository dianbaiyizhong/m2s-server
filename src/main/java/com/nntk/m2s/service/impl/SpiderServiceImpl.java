package com.nntk.m2s.service.impl;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Strings;
import com.nntk.m2s.mp.generate.entity.*;
import com.nntk.m2s.mp.generate.mapper.*;
import com.nntk.m2s.pojo.bo.MediaImage;
import com.nntk.m2s.pojo.bo.SinaNewsBo;
import com.nntk.m2s.service.IAiService;
import com.nntk.m2s.service.ISpiderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
        if (provinceMap.size() == 0) {
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

        for (int i = 1; i <= 10; i++) {
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
                    if (sources.size() > 0) {
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
                            imagesUrls = mediaImage.stream().map(MediaImage::getU).collect(Collectors.toList());
                            newsEntity.setThumbUrl(imagesUrls.get(0));
                            newsEntity.setImages(JSON.toJSONString(newsEntity.getImages()));
                        }
                        newsEntity.setType(0);
                        parseDetail(newsEntity);

                        if (newsEntity.getAreaLevel() == 4) {
                            log.warn("误判为国际新闻，跳过");
                            continue;
                        }
                        insertNews2Db(newsEntity);
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

        if (StringUtils.isEmpty(newsEntity.getContent())) {
            log.warn("由于新闻没有ai正文内容，判断新闻质量不高，未入库:{}", newsEntity.getTitle());
            return;
        }
        TNews news = new TNews();
        news.setTitle(newsEntity.getTitle());
        news.setNewsContext(newsEntity.getContent());
        news.setNewsTime(newsEntity.getNewsTime());
        news.setCreateTime(LocalDateTime.now());
        news.setAreaLevel(newsEntity.getAreaLevel());
        news.setAreaId(newsEntity.getPosInfoId());
        news.setThumbImg(newsEntity.getThumbUrl());
        news.setSourceName(newsEntity.getMediaName());
        news.setSourceUrl(newsEntity.getSourceUrl());
        news.setImages(newsEntity.getImages());
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
                    log.info("新闻标题已存在，跳过：{}", title);
                    continue;
                }


                newsEntity.setSourceUrl(sourceUrl);
                newsEntity.setType(1);

                parseDetail(newsEntity);
                insertNews2Db(newsEntity);

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


    private void parseDetail(SinaNewsBo sinaNewsBo) {
        String body = HttpUtil.createGet(sinaNewsBo.getSourceUrl()).execute().body();

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
        String text = htmlBody.select(".article").text();
        sinaNewsBo.setRawContent(text);

        String title = sinaNewsBo.getTitle();

        String prompt = title + """
                。
                这是最近热点新闻，请返回json格式，json对象包含type，content，area三个属性，json格式是为了方便解析，请别返回除了json之外其他内容
                请分析这个新闻具体发生的国家，省份，城市，地区。如果没有明显的地区性，type返回0
                如果有明显地区性，返回1，将发生地返回在area字段,area字段可以将地名逗号分隔拼起来返回字符串；
                将新闻内容整理成markdown格式放在content字段
                """;
        String deepSeekResponse = aiService.getDeepSeekResponse(prompt);

        JSONObject aiBody = JSON.parseObject(deepSeekResponse);
        sinaNewsBo.setContent(aiBody.getString("content"));
        if (aiBody.getInteger("type") == 0) {
            log.warn("这个新闻ai判断没有明显地区性。【{}】", sinaNewsBo.getTitle());
            matchText(sinaNewsBo);
        } else {
            log.info("aiBody:{}", aiBody);
            sinaNewsBo.setKeywords(aiBody.getString("area"));
            // 判断是否含有地名关键字
            String[] kewordsArray = sinaNewsBo.getKewordsArray();
            for (String keyword : kewordsArray) {
                Integer cityKey = getLikeByMap(cityMap, keyword);
                if (cityKey != null) {
                    sinaNewsBo.setAreaLevel(2);
                    sinaNewsBo.setPosInfoId(cityKey);
                    break;
                }
                Integer provinceKey = getLikeByMap(provinceMap, keyword);
                if (provinceKey != null) {
                    sinaNewsBo.setAreaLevel(1);
                    sinaNewsBo.setPosInfoId(provinceKey);
                    break;
                }
                Integer countryKey = getLikeByMap(countryMap, keyword);
                if (countryKey != null) {
                    sinaNewsBo.setAreaLevel(4);
                    sinaNewsBo.setPosInfoId(countryKey);
                    break;
                }
                log.info("ai返回的地名没有识别到，那就使用文本判断");
                matchText(sinaNewsBo);
            }
        }


    }

    private void matchText(SinaNewsBo sinaNewsBo) {
        boolean  matchContent= judgeByStr(sinaNewsBo, sinaNewsBo.getRawContent());
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

            if (keyLike.contains(entity.getValue())) {
                return entity.getKey();
            }
        }
        return null;
    }

}
