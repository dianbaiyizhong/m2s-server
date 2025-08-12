package com.nntk.m2s.config;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ReUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nntk.m2s.mp.generate.entity.TCctvList;
import com.nntk.m2s.mp.generate.mapper.TCctvListMapper;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class Timer2 {
    @Autowired
    private IVideoService videoService;

    @Autowired
    private TCctvListMapper cctvListMapper;

     @Scheduled(fixedDelay = 30000)
    public void scanVideo() throws IOException {

        List<TCctvList> tCctvLists = cctvListMapper.selectList(new QueryWrapper<TCctvList>().lambda()
                .eq(TCctvList::getStatus, 0)
                .orderByAsc(TCctvList::getId)
                .last("limit 1")
        );

        log.info("定时任务开始执行，{}", tCctvLists);

        for (int i = 0; i < tCctvLists.size(); i++) {
            TCctvList cctvList = tCctvLists.get(i);
            try {
                // 发送HTTP请求并获取文档对象
                Document doc = Jsoup.connect(cctvList.getUrl())
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .timeout(10000)
                        .get();

                // 选择新闻条目（根据实际网页结构调整选择器）
                String title = doc.title();
                String nameSpace = ReUtil.get("(\\d{8})", title, 0);
                int comboId = videoService.buildVideo(cctvList.getUrl(), nameSpace);


                cctvList.setStatus(1);
                cctvList.setComboId(comboId);
                cctvList.setCctvTime(nameSpace);

            } catch (Exception e) {
                cctvList.setStatus(-1);
                log.info(ExceptionUtil.getMessage(e));
            }

            cctvList.setCreateTime(LocalDateTime.now());
            cctvListMapper.updateById(cctvList);
        }

    }


}
