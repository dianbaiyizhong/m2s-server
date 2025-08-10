package com.nntk.m2s.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ByteUtil;
import cn.hutool.core.util.RuntimeUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Strings;
import com.nntk.m2s.constant.AreaLevelType;
import com.nntk.m2s.mp.generate.entity.TCountry;
import com.nntk.m2s.mp.generate.entity.TMapNewsPreview;
import com.nntk.m2s.mp.generate.entity.TNews;
import com.nntk.m2s.mp.generate.mapper.TCountryMapper;
import com.nntk.m2s.mp.generate.mapper.TMapNewsPreviewMapper;
import com.nntk.m2s.mp.generate.mapper.TNewsMapper;
import com.nntk.m2s.pojo.bo.SrtBo;
import com.nntk.m2s.repository.S3Repository;
import com.nntk.m2s.service.IAiService;
import com.nntk.m2s.service.IVideoService;
import com.nntk.m2s.utils.CollectionUtil;
import com.nntk.m2s.utils.DateUtils;
import com.nntk.m2s.utils.SrtReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class VideoServiceImpl implements IVideoService {

    @Autowired
    private IAiService aiService;

    @Autowired
    private TNewsMapper newsMapper;
    @Autowired
    private TCountryMapper countryMapper;


    @Autowired
    private S3Repository s3Repository;


    public static final String fileBasePath = "/Users/huanghaoming/Documents/新闻视频工作空间/";


    public static final String condaCommonEnvPath = "/Users/huanghaoming/miniconda3/envs/common/bin/python";

    public static final String condaFunclipEnvPath = "/Users/huanghaoming/miniconda3/envs/funclip/bin/python";

    public static final String videoHandleScriptPath = "/Users/huanghaoming/Documents/GitHub/map-news-spider-ai/python/m2sScript.py";

    public static final String spiltVideoScriptPath = "/Users/huanghaoming/Documents/GitHub/map-news-spider-ai/python/m2sSpiltVideo.py";
    public static final String funclipPath = "/Users/huanghaoming/Documents/GitHub/FunClip/funclip/videoclipper.py";

    public static final String ytDlpPath = "/Users/huanghaoming/miniconda3/envs/common/bin/yt-dlp";


    @Autowired
    private TMapNewsPreviewMapper mapNewsPreviewMapper;


    private void downloadYoutube(String url, String nameSpace) {

        String videoName = nameSpace + ".mp4";
        if (FileUtil.exist(fileBasePath + videoName)) {
            log.info("视频已存在...");
            return;
        }
        // 调用yt-dlp将视频下载到本地
        String cmd = ytDlpPath + " " +
                "-o " + fileBasePath + videoName + " " +
                url;
        String result = RuntimeUtil.execForStr("bash", "-c", cmd);
        log.info("yt-dlp download result:{}", result);
    }


    private void funclipVideo(String nameSpace) {
        if (FileUtil.exist(fileBasePath + nameSpace + "/funclip_output/total.srt")) {
            log.info("台词已存在...");
            return;
        } else {
            FileUtil.mkdir(fileBasePath + nameSpace + "/funclip_output");
        }
        String fullCommand = condaFunclipEnvPath + " " + funclipPath + " " +
                "--stage " + 1 + " " +
                "--file " + fileBasePath + nameSpace + "_spilt.mp4" + " " +
                "--output_dir " + fileBasePath + nameSpace + "/funclip_output";
        log.info("fullCommand:{}", fullCommand);

        log.info("Executing command: {}", fullCommand);

        String result = RuntimeUtil.execForStr("bash", "-c", fullCommand);
        log.info("funclip result:{}", result);

    }


    private void spiltVideo(String nameSpace) {
        if (FileUtil.exist(fileBasePath + nameSpace + "_spilt.mp4")) {
            log.info("视频已切割...");
            return;
        }
        String fullCommand = condaCommonEnvPath + " " + spiltVideoScriptPath + " " +
                " --input " + fileBasePath + nameSpace + ".mp4";
        String cmdResult = RuntimeUtil.execForStr("bash", "-c", fullCommand);
        if (cmdResult.contains("success")) {
            log.info("spiltVideo success");
        } else {
            throw new RuntimeException(cmdResult);
        }
    }


    @Override
    @Transactional
    public void buildVideo() {

        String youtubeVideoUrl = "https://www.youtube.com/watch?v=8bbx2YUiybU";
        // String nameSpace = DateUtils.getYmdNow();
        String nameSpace = "20250803";

        // 先生成一个comboId
        TMapNewsPreview mapNewsPreview = new TMapNewsPreview();

        mapNewsPreview.setCreateTime(DateUtils.getLocalDateTimeByYmd(nameSpace));
        mapNewsPreviewMapper.insert(mapNewsPreview);
        int comboId = mapNewsPreview.getId();

        // 定义从youtube下载下来的新闻原始视频文件名称

        downloadYoutube(youtubeVideoUrl, nameSpace);

        // 切割视频，只取视频后半段
        spiltVideo(nameSpace);

        // 调用funclip解析视频
        funclipVideo(nameSpace);


        List<SrtBo> srtBos = SrtReader.readSrtFile(fileBasePath + nameSpace + "/funclip_output/total.srt");


        String text = srtBos.stream()
                .map(SrtBo::getText)  // 获取name属性
                .collect(Collectors.joining(""));


        String bailianResponse = aiService.getBailianResponse(text, "26a82a01b5d443bf8fce0e4b0a3c5c8d");


        JSONArray jsonArray = JSON.parseArray(bailianResponse);


        List<Map<String, String>> newsMapList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            String content = jsonArray.getJSONObject(i).getString("content");
            String title = jsonArray.getJSONObject(i).getString("title");
            Map<String, String> newsMap = new HashMap<>();

            List<Integer> newsSet = new ArrayList<>();
            for (int j = 0; j < srtBos.size(); j++) {
                if (content.contains(srtBos.get(j).getText())) {
                    newsSet.add(j);
                }
            }
            log.info("新闻标题:{}", title);
            newsMap.put("title", title);
            if (!getAreaInfo(title, newsMap)) {
                log.info("未找到地区信息, 跳过该新闻:{}", title);
                continue;
            }
            List<Integer> longestConsecutive = CollectionUtil.findLongestConsecutive(newsSet);
            log.info("最长连续的字幕索引:{}", longestConsecutive);
            String startTime = srtBos.get(longestConsecutive.get(0)).getStartTime();
            String endTime = srtBos.get(longestConsecutive.get(longestConsecutive.size() - 1)).getEndTime();
            String videoName = nameSpace + "_00" + (i + 1);
            newsMap.put("videoFileName", videoName);


            if (FileUtil.exist(fileBasePath + videoName + ".mp4")) {
                log.warn("视频已存在:{}", title);
                newsMapList.add(newsMap);
                continue;
            }
            String fullCommand = condaCommonEnvPath + " " + videoHandleScriptPath + " " +
                    " --input " + fileBasePath + nameSpace + "_spilt.mp4" +
                    " --output " + fileBasePath + videoName + ".mp4" +
                    " --start " + convertToSeconds(startTime) +
                    " --end " + convertToSeconds(endTime);

            double duration = convertToSeconds(endTime) - convertToSeconds(startTime);
            if (duration <= 10) {
                log.warn("视频时长小于20秒, 跳过该新闻:{},{}", duration, title);
                continue;
            }

            String cmdResult = RuntimeUtil.execForStr("bash", "-c", fullCommand);
            if (!cmdResult.contains("success!")) {
                log.error("视频处理失败:{}", cmdResult);
            } else {
                log.info("视频处理成功:{}", title);
                newsMapList.add(newsMap);
            }

        }

        System.out.println(newsMapList);
        for (int i = 0; i < newsMapList.size(); i++) {
            int id = insertNews(newsMapList.get(i), comboId);
            if (i == 0) {
                mapNewsPreview.setCoverId(id);
                mapNewsPreview.setEnable(ByteUtil.intToByte(1));
                mapNewsPreviewMapper.updateById(mapNewsPreview);
            }
        }
    }


    private boolean getAreaInfo(String title, Map<String, String> map) {

        List<TCountry> tCountries = countryMapper.selectList(null);

        List<String> countryList = new ArrayList<>();
        for (int i = 0; i < tCountries.size(); i++) {
            if (title.contains(tCountries.get(i).getName())) {
                countryList.add(tCountries.get(i).getName());
            }
        }

        if (countryList.isEmpty()) {
            return false;
        }
        map.put("country", String.join(",", countryList));

        return true;
    }

    private int insertNews(Map<String, String> map, int comboId) {
        String subArea = null;
        TNews item = new TNews();
        item.setTitle(map.get("title"));
        String videoFileName = map.get("videoFileName");

        String countryName = map.get("country").split(",")[0];

        item.setAreaLevel(AreaLevelType.COUNTRY.getCode());
        item.setMapNews(true);
        TCountry country = countryMapper.selectOne(new QueryWrapper<TCountry>().lambda()
                .eq(TCountry::getName, countryName)
        );
        item.setAreaId(country.getId());
        item.setComboId(comboId);
        item.setCreateTime(LocalDateTime.now());
        // 新闻时间差不多就行
        item.setNewsTime(LocalDateTime.now());
        s3Repository.uploadFile(new File(fileBasePath + videoFileName + ".mp4"), "mapnews_video/" + videoFileName + ".mp4");


        File imageFile = new File(fileBasePath + videoFileName + "_thumb.png");
        if (FileUtil.exist(imageFile)) {
            s3Repository.uploadFile(imageFile, "mapnews_video/" + videoFileName + "_thumb.png");
            item.setThumbImg("https://map-question.gz.bcebos.com/" + "mapnews_video/" + videoFileName + "_thumb.png");
        }
        if (StringUtils.isNotEmpty(subArea)) {
            item.setLocationSubtitle(subArea);
        }
        item.setVideoUrl("https://map-question.gz.bcebos.com/" + "mapnews_video/" + videoFileName + ".mp4");
        newsMapper.insert(item);

        return item.getId();

    }


    private static double convertToSeconds(String timeString) {
        // 分割时:分:秒,毫秒
        String[] parts = timeString.split("[:,]");

        int hours = Integer.parseInt(parts[0]);    // 时
        int minutes = Integer.parseInt(parts[1]);  // 分
        int seconds = Integer.parseInt(parts[2]); // 秒
        int millis = Integer.parseInt(parts[3]);   // 毫秒

        // 计算总秒数 = 时*3600 + 分*60 + 秒 + 毫秒/1000.0
        return hours * 3600 + minutes * 60 + seconds + millis / 1000.0;
    }

}
