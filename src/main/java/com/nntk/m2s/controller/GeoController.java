package com.nntk.m2s.controller;

import com.nntk.m2s.pojo.vo.GeoResultVo;
import com.nntk.m2s.result.PageResult;
import com.nntk.m2s.result.RespBodyBuilder;
import com.nntk.m2s.result.ResultDataVo;
import com.nntk.m2s.service.IGeoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "geo")
@ResponseBody
public class GeoController {

    @Resource
    private IGeoService geoService;

//    @GetMapping("/list/bound")
//    public ResultDataVo list(@Valid MapQueryBoundForm form) {
//
//
//        PageResult<GeoResultVo> geoResultVoPageResult = geoService.listGeoByBound(form);
//
//
//        return RespBodyBuilder.success(geoResultVoPageResult);
//
//    }

    @GetMapping("/country/list")
    public ResultDataVo getCountryInfo() {

        PageResult<GeoResultVo> geoResultVoPageResult = geoService.getCountryInfo();
        return RespBodyBuilder.success(geoResultVoPageResult);

    }
}
