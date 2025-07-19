package com.nntk.m2s.service;

import com.nntk.m2s.mp.custom.entity.MapNewsCoverDTO;
import com.nntk.m2s.mp.generate.entity.TNews;
import com.nntk.m2s.pojo.form.NewsRequestForm;
import com.nntk.m2s.pojo.vo.NewsVo;
import com.nntk.m2s.result.PageResult;

import java.util.List;

public interface INewsService {

    public PageResult<NewsVo> listNews(NewsRequestForm form);


    public PageResult<NewsVo> getCctvList(int comboId);


    public PageResult<MapNewsCoverDTO> getMapNewsCoverList(Integer page, Integer rows);


}
