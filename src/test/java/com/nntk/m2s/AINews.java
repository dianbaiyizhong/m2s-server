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
                已知新闻分为 时政新闻，财经新闻，社会新闻，科技新闻，文化娱乐新闻，健康生活新闻，环境新闻，国际新闻等
                现有一个新闻标题为：山东财政组合拳精准扶持企业，200亿引导基金助力创新、10亿转贷基金护航小微
                请告诉我这是哪一类新闻
                """;

        String prompt2 = """
                89岁老太买菜回家被三轮车撞倒骨折，57岁男子早上喝酒后醉驾被查。这个新闻是否是一条地方新闻，如果是则返回发生地，如果不是则返回不是
                """;


        String deepSeekResponse = aiService.getRawDeepSeekResponse(prompt2);
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
