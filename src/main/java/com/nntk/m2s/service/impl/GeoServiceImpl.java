package com.nntk.m2s.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nntk.m2s.mp.generate.entity.TCountry;
import com.nntk.m2s.mp.generate.mapper.TCountryMapper;
import com.nntk.m2s.mp.generate.mapper.TProvinceMapper;
import com.nntk.m2s.pojo.vo.GeoResultVo;
import com.nntk.m2s.result.PageResult;
import com.nntk.m2s.service.IGeoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeoServiceImpl implements IGeoService {


//    @Autowired
//    private GeoSearchRepository geoSearchRepository;


    @Resource
    private TProvinceMapper provinceMapper;

    @Resource
    private TCountryMapper countryMapper;

    @Override
    public PageResult<GeoResultVo> getCountryInfo() {
        List<TCountry> tCountries = countryMapper.selectList(new QueryWrapper<TCountry>().lambda()
                .eq(TCountry::getEnable, true)
        );
        List<GeoResultVo> list = tCountries.stream().map(tCountry -> new GeoResultVo(
                tCountry.getId(),
                tCountry.getName(),
                tCountry.getCode(),
                tCountry.getLat(),
                tCountry.getLng()
        )).toList();
        return new PageResult(list.size(), list);
    }

//    @Override
//    public PageResult<GeoResultVo> listGeoByBound(MapQueryBoundForm mapQueryBoundForm) {
//
//
//        List<GeoProvincePO> geoProvincePOList = geoSearchRepository.searchWithinBounds(mapQueryBoundForm);
//
//        List<Integer> ids = geoProvincePOList.stream().map(geoProvincePO -> geoProvincePO.getId().intValue()).collect(Collectors.toList());
//
//
//        List<TProvince> tProvinces = provinceMapper.selectList(new QueryWrapper<TProvince>().lambda().in(TProvince::getId, ids));
//        return new PageResult(tProvinces.size(), tProvinces.stream()
//                .map(geoProvincePO -> new GeoResultVo(
//                                geoProvincePO.getId(),
//                                geoProvincePO.getName(),
//                                geoProvincePO.getCode(),
//                                geoProvincePO.getLat(),
//                                geoProvincePO.getLng()
//                        )
//                )
//                .toList());
//
//    }
}
