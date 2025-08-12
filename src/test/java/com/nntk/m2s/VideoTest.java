package com.nntk.m2s;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nntk.m2s.constant.AreaLevelType;
import com.nntk.m2s.mp.generate.entity.*;
import com.nntk.m2s.mp.generate.mapper.*;
import com.nntk.m2s.pojo.bo.SrtBo;
import com.nntk.m2s.repository.S3Repository;
import com.nntk.m2s.service.IAiService;
import com.nntk.m2s.service.INewsService;
import com.nntk.m2s.service.ISpiderService;
import com.nntk.m2s.service.IVideoService;
import com.nntk.m2s.utils.CollectionUtil;
import com.nntk.m2s.utils.SrtReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@Slf4j
public class VideoTest {

    @Autowired
    private IVideoService videoService;

    @Autowired
    private TCctvListMapper cctvListMapper;

    @Test
    void buildVideo() throws NoApiKeyException, InputRequiredException, IOException {


        List<String> strings = FileUtil.readLines("/Users/huanghaoming/Documents/新闻视频工作空间/cctv_youtube_list.txt", Charset.defaultCharset());

        for (int i = 0; i < strings.size(); i++) {
            String url = strings.get(i);
            TCctvList cctvList = new TCctvList();
            cctvList.setUrl(url);
            // boolean exists = cctvListMapper.exists(new QueryWrapper<TCctvList>().eq(TCctvList::getUrl, url));
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .timeout(10000)
                        .get();
                // 选择新闻条目（根据实际网页结构调整选择器）
                String title = doc.title();
                String nameSpace = ReUtil.get("(\\d{8})", title, 0);
                cctvList.setCctvTime(nameSpace);
                cctvListMapper.insert(cctvList);
            } catch (Exception e) {
                log.info(ExceptionUtil.getMessage(e));
            }
        }

//        List<TCctvList> tCctvLists = cctvListMapper.selectList(new QueryWrapper<TCctvList>().lambda()
//                .ne(TCctvList::getStatus, 1)
//                .last("limit 3")
//        );
//
//        for (int i = 0; i < tCctvLists.size(); i++) {
//            // 发送HTTP请求并获取文档对象

//            videoService.buildVideo("https://youtu.be/l7jeEtozNQA?si=UV8dzq8xFT9pAE0U", "20250711");
//        }


    }


}
