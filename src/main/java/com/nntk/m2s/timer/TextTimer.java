package com.nntk.m2s.timer;

import cn.hutool.core.util.ReUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nntk.m2s.mp.generate.entity.TCctvTask;
import com.nntk.m2s.mp.generate.mapper.TCctvTaskMapper;
import com.nntk.m2s.service.ISpiderService;
import com.nntk.m2s.service.IVideoService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@Conditional(TimerCondition.class)
public class TextTimer {
    @Autowired
    private ISpiderService spiderService;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private IVideoService videoService;
    @Autowired
    private TCctvTaskMapper cctvTaskMapper;

    @Scheduled(cron = "0 05 * * * *")
    public void scanNews() {
        try {
            spiderService.spiderI18nNews();
            spiderService.spiderChinaNews();
            log.info("采集新闻数据成功");
        } catch (Exception e) {
            log.error("定时任务执行异常", e);
        } finally {
            // 清理缓存
            cacheManager.getCache("news").clear();
        }

    }


}
