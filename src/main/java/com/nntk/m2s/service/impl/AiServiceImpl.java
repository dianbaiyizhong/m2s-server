package com.nntk.m2s.service.impl;

import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.nntk.m2s.mp.generate.entity.TAiCache;
import com.nntk.m2s.mp.generate.mapper.TAiCacheMapper;
import com.nntk.m2s.service.IAiService;
import com.nntk.m2s.utils.MarkdownUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

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
