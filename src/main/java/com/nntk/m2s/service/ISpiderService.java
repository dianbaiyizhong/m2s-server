package com.nntk.m2s.service;

import java.util.List;

public interface ISpiderService {

    public void spiderChinaNews();

    public void spiderI18nNews();


    public void spiderI18nNewsByUrl(List urls);


}
