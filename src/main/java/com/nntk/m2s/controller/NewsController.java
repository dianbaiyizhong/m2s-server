package com.nntk.m2s.controller;

import com.nntk.m2s.mp.custom.entity.MapNewsCoverDTO;
import com.nntk.m2s.pojo.form.BasePageForm;
import com.nntk.m2s.pojo.form.MoreNewsForm;
import com.nntk.m2s.pojo.form.NewsRequestForm;
import com.nntk.m2s.pojo.vo.NewsVo;
import com.nntk.m2s.repository.S3Repository;
import com.nntk.m2s.result.PageResult;
import com.nntk.m2s.result.RespBodyBuilder;
import com.nntk.m2s.result.ResultDataVo;
import com.nntk.m2s.service.INewsService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping(value = "news")
@ResponseBody
public class NewsController {

    @Resource
    private INewsService newsService;

    @GetMapping("/list")
    public ResultDataVo list(@Valid NewsRequestForm form) {


        PageResult<NewsVo> geoResultVoPageResult = newsService.listNews(form);

        return RespBodyBuilder.success(geoResultVoPageResult);
    }


    @GetMapping("/list/cctv/{comboId}")
    public ResultDataVo getCctvList(@PathVariable("comboId") int comboId) {
        PageResult<NewsVo> geoResultVoPageResult = newsService.getCctvList(comboId);
        return RespBodyBuilder.success(geoResultVoPageResult);
    }


    @GetMapping("/list/cctv/preview")
    public ResultDataVo getPreview(@Valid BasePageForm form) {
        PageResult<MapNewsCoverDTO> mapNewsCoverList = newsService.getMapNewsCoverList(form.getPage(), form.getRows());
        return RespBodyBuilder.success(mapNewsCoverList);
    }


    @GetMapping("/list/more")
    public ResultDataVo getMoreNews(@Valid MoreNewsForm form) {
        PageResult<NewsVo> newsVoPageResult = newsService.listMoreNews(form);
        return RespBodyBuilder.success(newsVoPageResult);
    }


}
