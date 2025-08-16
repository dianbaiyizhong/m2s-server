package com.nntk.m2s.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.nntk.m2s.constant.AreaLevelType;
import com.nntk.m2s.mp.custom.mapper.TMapNewsPreviewJoinMapper;
import com.nntk.m2s.mp.generate.entity.*;
import com.nntk.m2s.mp.generate.mapper.*;
import com.nntk.m2s.pojo.form.MoreNewsForm;
import com.nntk.m2s.pojo.form.NewsRequestForm;
import com.nntk.m2s.mp.custom.entity.MapNewsCoverDTO;
import com.nntk.m2s.pojo.vo.NewsVo;
import com.nntk.m2s.result.PageResult;
import com.nntk.m2s.service.IAiService;
import com.nntk.m2s.service.INewsService;
import com.nntk.m2s.utils.DateUtils;
import com.nntk.m2s.utils.mybatis.LastUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
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
    public PageResult<NewsVo> listMoreNews(MoreNewsForm moreNewsForm) {
        Page<TNews> page = new Page<>(moreNewsForm.getPage(), moreNewsForm.getRows());

        Page<TNews> tNewsPage = newsMapper.selectPage(page, new QueryWrapper<TNews>().lambda()
                .eq(TNews::getMapNews, false)
                .eq(TNews::getAreaId, moreNewsForm.getAreaId())
                .eq(TNews::getAreaLevel, moreNewsForm.getAreaLevel())
                .orderByDesc(TNews::getNewsTime)
        );
        List<NewsVo> mapNewsList = tNewsPage.getRecords().stream().map(o ->
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
                    dto.setContent(o.getNewsContent());
                    dto.setSourceName(o.getSourceName());
                    dto.setSourceUrl(o.getSourceUrl());
                    return dto;
                }
        ).toList();


        return new PageResult<>(page.getTotal(), mapNewsList);

    }


    @Override
    public PageResult<MapNewsCoverDTO> getMapNewsCoverList(Integer page, Integer rows) {


        MPJLambdaWrapper<TMapNewsPreview> wrapper = JoinWrappers.lambda(TMapNewsPreview.class)
                .eq(TMapNewsPreview::getEnable, true)
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
    @Cacheable(value = "news", key = "#form.date + '_' + #form.rangeType")
    public PageResult<NewsVo> listNews(NewsRequestForm form) {
        LocalDate oneWeekAgo = LocalDate.now().minusDays(1);
        String selectDay = DateUtils.getCurrentDay(form.getDate());
        String lastDay = DateUtils.getLastDay(form.getDate());

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
                .between(TNews::getFormattedNewsDate, lastDay, selectDay)
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
                    dto.setContent(o.getNewsContent());
                    dto.setNewType(o.getNewsType());
                    dto.setLocationSub(o.getLocationSubtitle());
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
                newsVo.setAreaImage(country.getImageUrl());
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
                newsVo.setAreaImage(province.getImageUrl());

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
                newsVo.setAreaImage(city.getImageUrl());
            }

        }

        ret.addAll(provinceList);
        ret.addAll(countryList);
        ret.addAll(cityList);
        ret = ret.stream()
                .sorted(Comparator.comparing(NewsVo::getArticleTime).reversed())
                .collect(Collectors.toList());


        // 按id1和id2分组，并计算每组的数量
        Map<List<Integer>, Long> countMap = ret.stream()
                .collect(Collectors.groupingBy(
                        bean -> List.of(bean.getAreaId(), bean.getAreaLevel()),
                        Collectors.counting()
                ));

        // 按id1和id2分组，并获取每组中时间最新的记录
        Map<List<Integer>, NewsVo> latestMap = ret.stream()
                .collect(Collectors.groupingBy(
                        bean -> List.of(bean.getAreaId(), bean.getAreaLevel()),
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparing(NewsVo::getArticleTime)),
                                optional -> optional.orElse(null)
                        )
                ));

        // 创建结果列表，设置num属性
        List<NewsVo> result = new ArrayList<>();
        for (Map.Entry<List<Integer>, NewsVo> entry : latestMap.entrySet()) {
            NewsVo bean = entry.getValue();
            Long count = countMap.get(entry.getKey());
            if (bean != null && count != null) {
                bean.setAggSum(count.intValue());
                result.add(bean);
            }
        }

        Collections.sort(result, new Comparator<NewsVo>() {
            @Override
            public int compare(NewsVo o1, NewsVo o2) {
                return o2.getArticleTime().compareTo(o1.getArticleTime());
            }
        });


        return new PageResult<>(ret.size(), result);

    }


}



