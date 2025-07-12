package com.nntk.m2s.service;

import com.nntk.m2s.pojo.vo.GeoResultVo;
import com.nntk.m2s.result.PageResult;

public interface IGeoService {


    public PageResult<GeoResultVo> getCountryInfo();

//    public PageResult<GeoResultVo> listGeoByBound(MapQueryBoundForm mapQueryBoundForm);

}
