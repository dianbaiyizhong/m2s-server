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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
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


    private Set<String> countryNameSet = new HashSet();

    @Autowired
    private TMapNewsPreviewMapper mapNewsPreviewMapper;


    private void downloadYoutube(String url, String nameSpace) throws InterruptedException, IOException {

        String videoName = "raw.mp4";
        String filePath = fileBasePath + nameSpace + "/";
        String videoPath = filePath + videoName;
        FileUtil.mkdir(filePath);
        if (FileUtil.exist(videoPath)) {
            log.info("视频已存在...");
            return;
        }
        // 调用yt-dlp将视频下载到本地
        String cmd = ytDlpPath + " " +
                "-o " + videoPath + " '" +
                url + "'";
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("bash", "-c", cmd);
        Process proc = pb.start();
        InputStream inputStream = proc.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String responseLine = "";
        while ((responseLine = reader.readLine()) != null) {
            System.out.println(responseLine);
            if (responseLine.contains("has already been downloaded")) {
                log.info("视频已存在，跳过下载:{}", videoPath);
                break;
            }
        }
        proc.waitFor();
        // 文件名可能叫raw.mp4.webm，将起改名字
        FileUtil.rename(new File(videoPath), "raw.mp4", true);

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
                "--file " + fileBasePath + nameSpace + "/raw_spilt.mp4" + " " +
                "--output_dir " + fileBasePath + nameSpace + "/funclip_output";
        log.info("fullCommand:{}", fullCommand);

        log.info("Executing command: {}", fullCommand);

        String result = RuntimeUtil.execForStr("bash", "-c", fullCommand);
        // log.info("funclip result:{}", result);

    }


    private void spiltVideo(String nameSpace) {
        if (FileUtil.exist(fileBasePath + nameSpace + "/raw_spilt.mp4")) {
            log.info("视频已切割...");
            return;
        }
        log.info("开始切割视频...");
        String fullCommand = condaCommonEnvPath + " " + spiltVideoScriptPath + " " +
                " --input " + fileBasePath + nameSpace + "/raw.mp4";
        String cmdResult = RuntimeUtil.execForStr("bash", "-c", fullCommand);
        if (cmdResult.contains("success")) {
            log.info("spiltVideo success");
        } else {
            throw new RuntimeException(cmdResult);
        }
    }


    @Override
    @Transactional
    public int buildVideo(String youtubeVideoUrl, String nameSpace) {

        // 初始化
        List<TCountry> tCountries = countryMapper.selectList(null);
        tCountries.forEach(tCountry -> {
            countryNameSet.add(tCountry.getName());
        });
        // 先生成一个comboId
        TMapNewsPreview mapNewsPreview = new TMapNewsPreview();

        mapNewsPreview.setCreateTime(DateUtils.getLocalDateTimeByYmd(nameSpace));
        mapNewsPreviewMapper.insert(mapNewsPreview);
        int comboId = mapNewsPreview.getId();

        // 定义从youtube下载下来的新闻原始视频文件名称

        try {
            downloadYoutube(youtubeVideoUrl, nameSpace);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 切割视频，只取视频后半段
        spiltVideo(nameSpace);

        // 调用funclip解析视频
        funclipVideo(nameSpace);


        List<SrtBo> srtBos = SrtReader.readSrtFile(fileBasePath + nameSpace + "/funclip_output/total.srt");


        String text = srtBos.stream()
                .map(SrtBo::getText)  // 获取name属性
                .collect(Collectors.joining(""));


        text = """
                %s。
                这段文本中拆分出有哪些是国际新闻，并返回拆分后的片段，以及一个比较清楚的新闻标题。返回json数组格式，每一个元素有两个属性，title代表新闻标题，content代表片段
                """.formatted(text);
        String deepSeekResponse = aiService.getDeepSeekResponse(text);

        JSONArray jsonArray = JSON.parseArray(deepSeekResponse);


        List<Map<String, String>> newsMapList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            String content = jsonArray.getJSONObject(i).getString("content");
            String title = jsonArray.getJSONObject(i).getString("title");
            Map<String, String> newsMap = new HashMap<>();

            List<Integer> newsSet = new ArrayList<>();
            for (int j = 0; j < srtBos.size(); j++) {
                if (content.contains(srtBos.get(j).getText())) {
                    newsSet.add(j + 1);
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
            if (longestConsecutive.isEmpty()) {
                continue;
            }
            String startTime = srtBos.get(longestConsecutive.get(0)).getStartTime();
            String endTime = srtBos.get(longestConsecutive.get(longestConsecutive.size() - 1)).getEndTime();
            String videoName = "video_00" + (i + 1);
            newsMap.put("videoFileName", videoName);


            if (FileUtil.exist(fileBasePath + videoName + ".mp4")) {
                log.warn("视频已存在:{}", title);
                newsMapList.add(newsMap);
                continue;
            }
            String fullCommand = condaCommonEnvPath + " " + videoHandleScriptPath + " " +
                    " --input " + fileBasePath + nameSpace + "/raw_spilt.mp4" +
                    " --output " + fileBasePath + nameSpace + "/" + videoName + ".mp4" +
                    " --start " + convertToSeconds(startTime) +
                    " --end " + convertToSeconds(endTime);

            int duration = convertToSeconds(endTime) - convertToSeconds(startTime);
            if (duration <= 25) {
                log.warn("视频时长小于25秒, 跳过该新闻:{},{}", duration, title);
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
        boolean isCover = false;
        for (int i = 0; i < newsMapList.size(); i++) {
            int id = insertNews(newsMapList.get(i), comboId, nameSpace);
            if (!isCover && id != 0) {
                mapNewsPreview.setCoverId(id);
                mapNewsPreview.setEnable(true);
                mapNewsPreviewMapper.updateById(mapNewsPreview);
                isCover = true;
            }
        }


        return comboId;
    }


    private boolean getAreaInfo(String title, Map<String, String> map) {
        List<String> countryList = new ArrayList<>();
        for (String tCountry : countryNameSet) {
            if (title.startsWith(tCountry)) {
                countryList.add(tCountry);
                break;
            }
        }

        if (countryList.isEmpty()) {
            // 找ai分析
            String country = aiService.getBailianResponse(title, "56f25c6a279f4e05a7a6825029674e15");
            log.info("ai识别地名:{}", country);
            if (countryNameSet.contains(country)) {
                countryList.add(country);
            }
        }
        if (countryList.isEmpty()) {
            return false;
        }
        map.put("country", String.join(",", countryList));
        return true;
    }

    private int insertNews(Map<String, String> map, int comboId, String nameSpace) {
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
        s3Repository.uploadFile(new File(fileBasePath + nameSpace + "/" + videoFileName + ".mp4"), "mapnews_video/" + nameSpace + "/" + videoFileName + ".mp4");


        File imageFile = new File(fileBasePath + nameSpace + "/" + videoFileName + "_thumb.png");
        if (FileUtil.exist(imageFile)) {
            s3Repository.uploadFile(imageFile, "mapnews_video/" + nameSpace + "/" + videoFileName + "_thumb.png");
            item.setThumbImg("https://map-question.gz.bcebos.com/" + "mapnews_video/" + nameSpace + "/" + videoFileName + "_thumb.png");
        }
        if (StringUtils.isNotEmpty(subArea)) {
            item.setLocationSubtitle(subArea);
        }
        item.setVideoUrl("https://map-question.gz.bcebos.com/" + "mapnews_video/" + nameSpace + "/" + videoFileName + ".mp4");

        boolean existCountry = newsMapper.exists(new QueryWrapper<TNews>().lambda()
                .eq(TNews::getComboId, comboId)
                .eq(TNews::getAreaLevel, AreaLevelType.COUNTRY.getCode())
                .eq(TNews::getAreaId, country.getId())
        );

        if (existCountry) {
            log.info("已经存在相同国家");
            return 0;
        }

        boolean exist = newsMapper.exists(new QueryWrapper<TNews>().lambda()
                .eq(TNews::getTitle, item.getTitle())
        );

        if (exist) {
            log.info("已经存在相同新闻:{}", item.getTitle());
            return 0;
        }

        newsMapper.insert(item);

        return item.getId();

    }


    private static int convertToSeconds(String timeString) {
        // 分割时:分:秒,毫秒
        String[] parts = timeString.split("[:,]");

        int hours = Integer.parseInt(parts[0]);    // 时
        int minutes = Integer.parseInt(parts[1]);  // 分
        int seconds = Integer.parseInt(parts[2]); // 秒
        int millis = Integer.parseInt(parts[3]);   // 毫秒

        // 计算总秒数 = 时*3600 + 分*60 + 秒 + 毫秒/1000.0
        return (int) (hours * 3600 + minutes * 60 + seconds + millis / 1000.0);
    }


    public static void processList(List<Map<String, String>> list) {
        // 用于记录已经出现过的国家
        Set<String> seenCountries = new HashSet<>();

        // 遍历列表中的每个map
        for (Map<String, String> map : list) {
            if (map.containsKey("country")) {
                String countryValue = map.get("country");

                // 分割国家字符串
                List<String> countries = Arrays.stream(countryValue.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();

                // 保留未出现过的国家
                List<String> newCountries = new ArrayList<>();
                for (String country : countries) {
                    if (!seenCountries.contains(country)) {
                        newCountries.add(country);
                        seenCountries.add(country);
                    }
                }

                // 更新map中的country值
                if (newCountries.isEmpty()) {
                    map.remove("country"); // 如果所有国家都已存在，则移除该属性
                } else {
                    map.put("country", String.join(",", newCountries));
                }
            }
        }
    }
}
