package com.nntk.m2s.config;

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
public class Timer {
    @Autowired
    private ISpiderService spiderService;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private IVideoService videoService;
    @Autowired
    private TCctvTaskMapper cctvTaskMapper;

    @Scheduled(cron = "0 30 * * * *")
    @Conditional(TimerCondition.class)
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


    @Scheduled(cron = "0 30 * * * *")
    @Conditional(LocalTimerCondition.class)
    public void scanVideo() {

        String nameSpace = null;

        String url = "https://www.youtube.com/watch?v=J-mixttkETs&list=PL0eGJygpmOH5xQuy8fpaOvKrenoCsWrKh&index=1";
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(10000)
                    .get();
            String title = doc.title();
            nameSpace = ReUtil.get("(\\d{8})", title, 0);
        } catch (Exception e) {
            log.error("抓youtube报错", e);
            return;
        }

        boolean exists = cctvTaskMapper.exists(new QueryWrapper<TCctvTask>().lambda()
                .eq(TCctvTask::getCctvTime, nameSpace)
        );
        if (exists) {
            log.info("已经存在任务，{}", nameSpace);
            return;
        }

        try {
            log.info("定时任务开始执行，{}", nameSpace);
            int comboId = videoService.buildVideo(url, nameSpace);

            TCctvTask tCctvTask = new TCctvTask();
            tCctvTask.setStatus(1);
            tCctvTask.setComboId(comboId);
            tCctvTask.setCctvTime(nameSpace);
            tCctvTask.setCreateTime(LocalDateTime.now());
            cctvTaskMapper.insert(tCctvTask);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
