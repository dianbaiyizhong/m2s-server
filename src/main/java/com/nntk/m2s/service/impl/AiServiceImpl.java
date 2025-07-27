package com.nntk.m2s.service.impl;

import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.nntk.m2s.constant.CommonConst;
import com.nntk.m2s.mp.generate.entity.TAiCache;
import com.nntk.m2s.mp.generate.mapper.TAiCacheMapper;
import com.nntk.m2s.service.IAiService;
import com.nntk.m2s.utils.MarkdownUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class AiServiceImpl implements IAiService {

    private final ChatClient chatClient;


    @Resource
    private TAiCacheMapper aiCacheMapper;

    public AiServiceImpl(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public String getDeepSeekResponse(String prompt) {

        TAiCache aiCache = aiCacheMapper.selectOne(new QueryWrapper<TAiCache>()
                .lambda()
                .eq(TAiCache::getPromptMd5, MD5.create().digestHex(prompt))
        );
        if (aiCache != null) {
            return MarkdownUtils.getJson(aiCache.getContent());
        }
        String content = chatClient.prompt(prompt).call().content();
        aiCache = new TAiCache();
        aiCache.setPromptMd5(MD5.create().digestHex(prompt));
        aiCache.setPrompt(prompt);
        aiCache.setContent(content);
        aiCacheMapper.insert(aiCache);
        return MarkdownUtils.getJson(content);
    }

    @Override
    public String getBailianResponse(String prompt) {


        TAiCache aiCache = aiCacheMapper.selectOne(new QueryWrapper<TAiCache>()
                .lambda()
                .eq(TAiCache::getPromptMd5, MD5.create().digestHex(prompt))
        );
        if (aiCache != null) {
            return MarkdownUtils.getJson(aiCache.getContent());
        }
        ApplicationParam param = ApplicationParam.builder()
                // 若没有配置环境变量，可用百炼API Key将下行替换为：.apiKey("sk-xxx")。但不建议在生产环境中直接将API Key硬编码到代码中，以减少API Key泄露风险。
                .apiKey("sk-cb80f6a73a5f4ffb80b12f3260eb7217")
                .appId("3612d3d43acf4b77b695c0859d7a1da9")
                .enableWebSearch(true)
                .prompt(prompt)
                .build();

        Application application = new Application();
        String content = null;
        try {
            ApplicationResult result = application.call(param);
            content = result.getOutput().getText();
        } catch (Exception e) {
            log.error("百炼报错:{}", e.getMessage());
            content = e.getMessage();
        }
        if (content.contains(CommonConst.NEWS_NOT_FOUND)) {
            return CommonConst.NEWS_NOT_FOUND;
        }
        aiCache = new TAiCache();
        aiCache.setPromptMd5(MD5.create().digestHex(prompt));
        aiCache.setPrompt(prompt);
        aiCache.setContent(content);
        aiCache.setCreateTime(LocalDateTime.now());
        aiCacheMapper.insert(aiCache);
        return MarkdownUtils.getJson(content);
    }

    @Override
    public String getRawDeepSeekResponse(String prompt) {
        String content = chatClient.prompt(prompt).call().content();
        return content;
    }

    @Override
    public List<String> getBrowserUseResponse(List<String> prompts) {
        List<String> responseList = Lists.newArrayList();
        List<List<String>> partition = Lists.partition(prompts, 2);
        for (List<String> part : partition) {
            JSONObject body = new JSONObject();
            log.info("prompt:{}", part);
            body.put("prompts", part);
            HttpResponse response = HttpUtil.createPost("http://localhost:8000/browserUse/chat")
                    .body(body.toJSONString())
                    .execute();
            log.info("response:{}", response.body());
            List<String> strings = JSON.parseArray(response.body(), String.class);
            responseList.addAll(strings);
        }
        return responseList;
    }
}
