package com.nntk.m2s.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.google.common.collect.Lists;
import com.nntk.m2s.constant.AreaLevelType;
import com.nntk.m2s.exception.NoDataException;
import com.nntk.m2s.mp.custom.mapper.TMapNewsPreviewJoinMapper;
import com.nntk.m2s.mp.generate.entity.*;
import com.nntk.m2s.mp.generate.mapper.*;
import com.nntk.m2s.pojo.form.NewsRequestForm;
import com.nntk.m2s.mp.custom.entity.MapNewsCoverDTO;
import com.nntk.m2s.pojo.vo.NewsVo;
import com.nntk.m2s.result.PageResult;
import com.nntk.m2s.service.IAiService;
import com.nntk.m2s.service.INewsService;
import com.nntk.m2s.utils.mybatis.LastUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NewsServiceImpl implements INewsService {

    @Resource
    private TNewsMapper newsMapper;

    @Resource
    private TProvinceMapper provinceMapper;

    @Resource
    private TCountryMapper countryMapper;

    @Resource
    private TCityMapper cityMapper;


    @Resource
    private TMapNewsPreviewJoinMapper mapNewsPreviewMapper;


    @Resource
    private IAiService aiService;


    @Override
    public PageResult<MapNewsCoverDTO> getMapNewsCoverList(Integer page, Integer rows) {


        MPJLambdaWrapper<TMapNewsPreview> wrapper = JoinWrappers.lambda(TMapNewsPreview.class)
                .selectAll(TMapNewsPreview.class)
                .selectAll(TNews.class)
                .selectAs(TNews::getTitle, MapNewsCoverDTO::getNewsTitle)
                .selectAs(TMapNewsPreview::getTitle, MapNewsCoverDTO::getCoverTitle)
                .leftJoin(TNews.class, TNews::getId, TMapNewsPreview::getCoverId)
                .orderByDesc(TMapNewsPreview::getCreateTime);
        Page<MapNewsCoverDTO> mapNewsCoverPOPage = mapNewsPreviewMapper.selectJoinPage(new Page<>(page, rows), MapNewsCoverDTO.class, wrapper);
        return new PageResult<>(mapNewsCoverPOPage.getTotal(), mapNewsCoverPOPage.getRecords());
    }

    @Override
    public PageResult<NewsVo> listNews(NewsRequestForm form) {
        LocalDate oneWeekAgo = LocalDate.now().minusDays(50);

        List<TNews> mapNewsDBList = newsMapper.selectList(new QueryWrapper<TNews>().lambda()
                .isNotNull(TNews::getAreaLevel)
                .eq(TNews::getMapNews, false).ne(TNews::getAreaId, 0)
                .and(ObjectUtils.nullSafeEquals(form.getRangeType(), 1), wrapper -> wrapper
                        .or().eq(TNews::getAreaLevel, AreaLevelType.PROVINCE.getCode())
                        .or().eq(TNews::getAreaLevel, AreaLevelType.CITY.getCode())
                )
                .and(ObjectUtils.nullSafeEquals(form.getRangeType(), 2), wrapper -> wrapper
                        .or().eq(TNews::getAreaLevel, AreaLevelType.COUNTRY.getCode())
                )
                .apply("date_format (news_time,'%Y-%m-%d') >= '" + oneWeekAgo + "'")
                .orderByDesc(TNews::getNewsTime)
        );
        return wrapperMapNews(mapNewsDBList);
    }

    @Override
    public PageResult<NewsVo> getCctvList(int comboId) {


        List<NewsVo> result = new ArrayList<>();
        List<TNews> mapNewsDBList = newsMapper.selectList(new LambdaQueryWrapper<TNews>()
                .eq(TNews::getComboId, comboId)
                .eq(TNews::getMapNews, true)
        );


        List<Integer> areaIds = mapNewsDBList.stream()
                .map(TNews::getAreaId)
                .collect(Collectors.toList());


        List<TCountry> tCountries = countryMapper.selectList(new QueryWrapper<TCountry>().lambda()
                .in(TCountry::getId, areaIds)
                .last("ORDER BY FIELD(id," + StringUtils.join(areaIds, ",") + ")")
        );


        for (int i = 0; i < mapNewsDBList.size(); i++) {
            TNews tMapNewsDetail = mapNewsDBList.get(i);
            TCountry tCountry = tCountries.get(i);
            result.add(
                    NewsVo.builder()
                            .title(tMapNewsDetail.getTitle())
                            .lat(tCountry.getLat())
                            .lng(tCountry.getLng())
                            .code(tCountry.getCode())
                            .locationSub(tMapNewsDetail.getLocationSubtitle())
                            .name(tCountry.getName())
                            .videoUrl(tMapNewsDetail.getVideoUrl())
                            .areaId(tMapNewsDetail.getAreaId())
                            .build()
            );

        }

        return new PageResult<>(result.size(), result);

    }


    private PageResult<NewsVo> wrapperMapNews(List<TNews> mapNewsDBList) {


        List<NewsVo> mapNewsList = mapNewsDBList.stream().map(o ->
                {
                    NewsVo dto = new NewsVo();
                    dto.setAreaId(o.getAreaId());
                    dto.setAreaLevel(o.getAreaLevel());
                    dto.setTitle(o.getTitle());
                    long timestampUtc = o.getNewsTime()
                            .atZone(ZoneId.of("Asia/Shanghai"))
                            .toInstant()
                            .toEpochMilli();
                    dto.setArticleTime(timestampUtc);
                    dto.setThumbImg(o.getThumbImg());
                    dto.setContext(o.getNewsContext());
                    dto.setSourceName(o.getSourceName());
                    dto.setSourceUrl(o.getSourceUrl());
                    return dto;
                }
        ).toList();

        List<NewsVo> provinceList = mapNewsList.stream()
                .filter(item -> item.getAreaLevel() == AreaLevelType.PROVINCE.getCode()
                ).collect(Collectors.toList());

        List<NewsVo> cityList = mapNewsList.stream()
                .filter(item -> item.getAreaLevel() == AreaLevelType.CITY.getCode()
                ).collect(Collectors.toList());

        List<NewsVo> countryList = mapNewsList.stream()
                .filter(item -> item.getAreaLevel() == AreaLevelType.COUNTRY.getCode()
                ).collect(Collectors.toList());
        List<NewsVo> ret = new ArrayList<>();


        List<Integer> provinceIds = provinceList.stream().map(NewsVo::getAreaId).collect(Collectors.toList());
        List<Integer> cityIds = cityList.stream().map(NewsVo::getAreaId).collect(Collectors.toList());
        List<Integer> countryIds = countryList.stream().map(NewsVo::getAreaId).collect(Collectors.toList());
        if (!countryIds.isEmpty()) {
            List<TCountry> countries = countryMapper.selectList(new QueryWrapper<TCountry>().lambda()
                    .in(TCountry::getId, countryIds)
                    .last(LastUtils.orderByIntField("id", countryIds))
            );

            Map<Integer, TCountry> countryMap = countries.stream().collect(Collectors.toMap(TCountry::getId, Function.identity()));

            for (NewsVo newsVo : countryList) {
                TCountry country = countryMap.get(newsVo.getAreaId());
                newsVo.setLat(country.getLat());
                newsVo.setLng(country.getLng());
                newsVo.setIntro(country.getIntro());
                newsVo.setName(country.getName());
                newsVo.setCode(country.getCode());
            }
        }
        if (!provinceIds.isEmpty()) {
            List<TProvince> provinces = provinceMapper.selectList(new QueryWrapper<TProvince>().lambda()
                    .in(TProvince::getId, provinceIds)
                    .last(LastUtils.orderByIntField("id", provinceIds))
            );

            Map<Integer, TProvince> provinceMap = provinces.stream().collect(Collectors.toMap(TProvince::getId, Function.identity()));


            for (NewsVo newsVo : provinceList) {
                TProvince province = provinceMap.get(newsVo.getAreaId());
                newsVo.setLat(province.getLat());
                newsVo.setLng(province.getLng());
                newsVo.setIntro(province.getIntro());
                newsVo.setName(province.getName());
                newsVo.setCode(province.getCode());
                newsVo.setNcpSlogan(province.getNcpSlogan());
                newsVo.setProvinceShortHand(province.getShorthand());
                newsVo.setThemeColor(province.getBgColor());
            }
        }
        if (!cityIds.isEmpty()) {
            List<TCity> citys = cityMapper.selectList(new QueryWrapper<TCity>().lambda()
                    .in(TCity::getId, cityIds)
                    .last(LastUtils.orderByIntField("id", cityIds))
            );
            Map<Integer, TCity> cityMap = citys.stream().collect(Collectors.toMap(TCity::getId, Function.identity()));

            for (NewsVo newsVo : cityList) {
                TCity city = cityMap.get(newsVo.getAreaId());

                newsVo.setLat(city.getLat());
                newsVo.setLng(city.getLng());
                newsVo.setIntro(city.getIntro());
                newsVo.setName(city.getName());
                newsVo.setCode(city.getAreaCode());
                newsVo.setProvinceShortHand(city.getProvinceShortHand());
                newsVo.setLicensePlateNum(city.getLicensePlateNum());
            }

        }

        ret.addAll(provinceList);
        ret.addAll(countryList);
        ret.addAll(cityList);
        ret = ret.stream()
                .sorted(Comparator.comparing(NewsVo::getArticleTime).reversed())
                .collect(Collectors.toList());

        return new PageResult<>(ret.size(), ret);

    }


    @Override
    public void spiderNewsListByAi(int type, List<TNews> dbResult) {


        String prompt = """
                    打开%s，获取最新消息中的新闻列表，将标题返回为title，新闻链接返回为url
                    最终严格返回一个数组json，别带上任何其他内容，方便解析。标题可能有特殊符号，返回的json请帮我特殊处理
                """;
        if (type == 1) {
            prompt = String.format(prompt, "https://news.sina.com.cn/china/");

        } else if (type == 2) {
            prompt = String.format(prompt, "https://news.sina.com.cn/world/");

        } else {
            throw new NoDataException("暂不支持该类型的新闻抓取");
        }


        String result = aiService.getBrowserUseResponse(Lists.newArrayList(prompt)).get(0);
        // 省钱模式
        // String result = spiderChinaNewsTitle().get(0);

        List<String> titleList = new ArrayList<>();
        JSONArray jsonArray = JSON.parseArray(result);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            String title = item.getString("title");
            String sourceUrl = item.getString("url");

            boolean exists = newsMapper.exists(new QueryWrapper<TNews>().lambda()
                    .eq(TNews::getTitle, title)
            );
            if (exists) {
                log.info("新闻标题已存在，跳过：{}", title);
                continue;
            }

            TNews tNews = new TNews();


            String promptItem = """
                        打开%s，
                        获取新闻标题（newsTitle）;
                        获取新闻时间（newsTime），格式转成（yyyy-MM-ddThh:mm:ss）;
                        来源（newsSource），尽可能根据文章中明确有【来源】两个字来判断;
                        新闻的第一张图片url（newsUrl），如果没有http前缀，请加上;如果找不到图片，则返回null即可
                        最终严格返回一个json，别带上任何其他内容，方便解析。新闻标题可能有特殊符号，返回的json请帮我特殊处理
                    """;
            promptItem = String.format(promptItem, sourceUrl);
            titleList.add(promptItem);

            tNews.setMapNews(false);
            tNews.setTitle(title);
            tNews.setSourceUrl(sourceUrl);
            dbResult.add(tNews);
        }

        List<String> itemResult = aiService.getBrowserUseResponse(Lists.newArrayList(titleList));
        for (int i = 0; i < itemResult.size(); i++) {
            try {
                JSONObject newsItem = JSON.parseObject(itemResult.get(i));
                dbResult.get(i).setSourceName(newsItem.getString("newsSource"));
                dbResult.get(i).setNewsTime(LocalDateTime.parse(newsItem.getString("newsTime")));
                dbResult.get(i).setThumbImg(newsItem.getString("newsUrl"));
                dbResult.get(i).setTitle(newsItem.getString("newsTitle"));
                dbResult.get(i).setNewsContext(newsItem.getString("newsContext"));
                dbResult.get(i).setCreateTime(LocalDateTime.now());
            } catch (Exception e) {
                e.printStackTrace();
                log.error("解析新闻数据失败，可能是格式不对，跳过该条数据：{}", itemResult.get(i));
            }

        }
    }

    @Override
    public void scanInternational() {

        List<JSONObject> countryInfoList = countryMapper.selectList(null).stream().map(tCountry -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", tCountry.getId());
            jsonObject.put("type", 4);
            jsonObject.put("name", tCountry.getName());
            return jsonObject;
        }).collect(Collectors.toList());

        List<TNews> dbResult = new ArrayList<>();

        spiderNewsListByAi(2, dbResult);

        {
            List<String> titleList = new ArrayList<>();
            titleList = dbResult.stream().map(TNews::getTitle).collect(Collectors.toList());
            String prompt =
                    "%s，这里有个json数组A，其中id是国家id，name是国家名字。请先学习理解。" +
                            "有一个json数组B,%s,标题是title，代表新闻标题。按顺序查出每一个新闻的头条，帮我查到他是在哪个国家发生的，如果查不到，请不要勉强，返回0即可，并带上第一个数组A里的的国家id，属性名是areaId，严格返回格式json数组，不需要包装。";
            prompt = String.format(prompt, JSON.toJSONString(countryInfoList), JSON.toJSONString(titleList));
            String deepSeekResponse = aiService.getDeepSeekResponse(prompt);
            JSONArray jsonArray = JSON.parseArray(deepSeekResponse);
            for (int i = 0; i < dbResult.size(); i++) {
                TNews tNews = dbResult.get(i);
                tNews.setAreaLevel(AreaLevelType.COUNTRY.getCode());
                tNews.setAreaId(jsonArray.getJSONObject(i).getInteger("areaId"));
            }
            newsMapper.insert(dbResult);
        }
    }

    @Override
    public void scanChina() {

        List<JSONObject> provinceInfoList = provinceMapper.selectList(null).stream().map(tProvince -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", tProvince.getId());
            jsonObject.put("type", 1);
            jsonObject.put("name", tProvince.getName());
            return jsonObject;
        }).collect(Collectors.toList());


        List<JSONObject> cityInfoList = cityMapper.selectList(null).stream().map(tCity -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", tCity.getId());
            jsonObject.put("type", 2);
            jsonObject.put("name", tCity.getName());
            return jsonObject;
        }).collect(Collectors.toList());
        // 合并
        provinceInfoList.addAll(cityInfoList);
        List<JSONObject> allInfoList = provinceInfoList;

        List<TNews> dbResult = new ArrayList<>();


        spiderNewsListByAi(1, dbResult);


        {
            List<String> titleList = new ArrayList<>();
            titleList = dbResult.stream().map(TNews::getTitle).collect(Collectors.toList());
            String prompt =
                    "%s，这里有个json数组A，其中type=1代表省份，type=2代表城市，name是地方名。请先学习理解。" +
                            "有一个json数组B,%s,每一个元素是一个最近的热点新闻标题。按顺序查出每一个新闻的头条，帮我查到他是在哪个省份发生的，如果具体到了城市，那就按照城市为准，带上数组A里的type，属性名是type；带上数组A里的的id，属性名是areaId，如果查不到，请不要勉强，返回0即可；严格返回格式json数组，不需要包装。";
            prompt = String.format(prompt, JSON.toJSONString(allInfoList), JSON.toJSONString(titleList));
            String deepSeekResponse = aiService.getDeepSeekResponse(prompt);
            try {
                JSONArray jsonArray = JSON.parseArray(deepSeekResponse);
                for (int i = 0; i < dbResult.size(); i++) {
                    TNews tNews = dbResult.get(i);
                    tNews.setAreaLevel(jsonArray.getJSONObject(i).getInteger("type"));
                    tNews.setAreaId(jsonArray.getJSONObject(i).getInteger("areaId"));
                    log.info("insert news:{}", tNews);
                }
            } catch (Exception e) {
                log.warn(e.getMessage());
            } finally {
                newsMapper.insert(dbResult);
            }

        }

    }


}
