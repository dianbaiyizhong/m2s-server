package com.nntk.m2s.repository;

import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.HttpUtil;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;
import com.baidubce.services.bos.model.PutObjectResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nntk.m2s.mp.generate.entity.TAiCache;
import com.nntk.m2s.mp.generate.entity.THttpCache;
import com.nntk.m2s.mp.generate.mapper.TAiCacheMapper;
import com.nntk.m2s.mp.generate.mapper.THttpCacheMapper;
import com.nntk.m2s.utils.MarkdownUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.time.LocalDateTime;

@Repository
@Slf4j
public class HttpRepository {

    @Resource
    private THttpCacheMapper httpCacheMapper;

    public String get(String url) {
        THttpCache cache = httpCacheMapper.selectOne(new QueryWrapper<THttpCache>()
                .lambda()
                .eq(THttpCache::getUrl, url)
        );
        if (cache != null) {
            return cache.getContent();
        }
        String body = HttpUtil.createGet(url).execute().body();
        cache = new THttpCache();
        cache.setContent(body);
        cache.setUrl(url);
        cache.setCreateTime(LocalDateTime.now());
        httpCacheMapper.insert(cache);
        return body;
    }


}
