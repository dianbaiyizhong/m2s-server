package com.nntk.m2s.config;

import com.nntk.m2s.service.ISpiderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Conditional(TimerCondition.class)
public class Timer {
    @Autowired
    private ISpiderService spiderService;

    @Autowired
    private CacheManager cacheManager;


    @Scheduled(cron = "0 30 * * * *")
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
