package com.nntk.m2s;

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
    void scanAll() {
        newsService.scanChina();
        newsService.scanInternational();
    }

    @Test
    void testAiChat() {

//        String prompt = """
//                中国证监会原法律部副主任吴国舫被“双开”
//
//                这是最近热点新闻，请返回json格式，json对象包含type，content，area三个属性，json格式是为了方便解析，请别返回除了json之外其他内容
//                请分析这个新闻具体发生的国家，省份，城市，地区。如果没有明显的地区性，type返回0
//                如果有明显地区性，返回1，将发生地返回在area字段,area字段可以将地名逗号分隔拼起来返回字符串；
//                将新闻内容整理成markdown格式放在content字段
//                """;
//        String deepSeekResponse = aiService.getRawDeepSeekResponse(prompt);
//        System.out.println(deepSeekResponse);
    }


}
