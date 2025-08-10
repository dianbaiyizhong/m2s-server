package com.nntk.m2s;

import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.google.common.collect.Lists;
import com.nntk.m2s.constant.CommonConst;
import com.nntk.m2s.mp.generate.mapper.TCityMapper;
import com.nntk.m2s.mp.generate.mapper.TCountryMapper;
import com.nntk.m2s.mp.generate.mapper.TNewsMapper;
import com.nntk.m2s.mp.generate.mapper.TProvinceMapper;
import com.nntk.m2s.service.IAiService;
import com.nntk.m2s.service.INewsService;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Slf4j
class AINews {


    @Autowired
    private TProvinceMapper provinceMapper;

    @Autowired
    private TCountryMapper countryMapper;


    @Autowired
    private TCityMapper cityMapper;


    @Autowired
    private TNewsMapper newsMapper;

    @Autowired
    private IAiService aiService;


    @Autowired
    private INewsService newsService;


    @Test
    void testAiChat() {

        String prompt = """
                %s。
                以上是一个网络新闻标题，请联网搜索，返回一个json对象，包含type，area两个属性;
                type：如果这是一条地方新闻返回1，否则返回0；如果等于0，就不需要area，返回空即可;
                如果这是一条国际新闻，则判断是否与与特定的国家关联上。如果是，也可以返回type为1
                area：发生地（xxx国，xxx省,xxx市）如果具体不到城市，那就返回省份;如果是外国的，那就返回国名例如xxx国
                """.formatted("河北兴隆县六道河镇救灾进展：20个村中19个已全部或部分恢复通电通讯");
        String deepSeekResponse = aiService.getBailianResponse(prompt);
        System.out.println(deepSeekResponse);
    }


    @Test
    void testBailian() throws NoApiKeyException, InputRequiredException {

        String prompt = """
                %s。返回这个新闻概要，要求markdown格式"""
                .formatted("印度一女子应聘军警晕倒后在救护车上被轮奸，当地卫生部门甩锅给私人机构", CommonConst.NEWS_NOT_FOUND);

        System.out.println(prompt);

        String bailianResponse = aiService.getBailianResponse(prompt);
        System.out.println(bailianResponse);
    }


}
