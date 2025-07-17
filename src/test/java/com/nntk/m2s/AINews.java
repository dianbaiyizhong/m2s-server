package com.nntk.m2s;

import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.google.common.collect.Lists;
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
                16岁女子飞机上分娩疑携带传染病？急盼公开通报回应网络传言。
                以上是一个网络新闻标题，请联网搜索，返回一个json对象，包含type，area两个属性;
                type：如果这是一条地方新闻返回1，否则返回0；如果等于0，就不需要area，返回空即可;
                如果这是一条国际新闻，则判断是否与与特定的国家关联上。如果是，也可以返回type为1
                area：发生地（xxx国，xxx省,xxx市）如果具体不到城市，那就返回省份;如果是外国的，那就返回国名例如xxx国
                                
                """;
        ApplicationParam param = ApplicationParam.builder()
                // 若没有配置环境变量，可用百炼API Key将下行替换为：.apiKey("sk-xxx")。但不建议在生产环境中直接将API Key硬编码到代码中，以减少API Key泄露风险。
                .apiKey("sk-cb80f6a73a5f4ffb80b12f3260eb7217")
                .appId("3612d3d43acf4b77b695c0859d7a1da9")
                .enableWebSearch(true)
                .prompt(prompt)
                .build();

        Application application = new Application();
        ApplicationResult result = application.call(param);

        System.out.println(result.getOutput().getText());
    }


}
