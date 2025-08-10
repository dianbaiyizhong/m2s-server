package com.nntk.m2s.utils;

import cn.hutool.core.io.FileUtil;
import com.nntk.m2s.pojo.bo.SrtBo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class SrtReader {

    public static List<SrtBo> readSrtFile(String filePath) {
        List<SrtBo> srtBos = new ArrayList<>();


        List<String> lines = FileUtil.readLines(new File(filePath), Charset.defaultCharset());


        for (int i = 0; i < lines.size(); i = i + 3) {
            SrtBo srtBo = new SrtBo();
            String index = lines.get(i);
            String timeLine = lines.get(i + 1);
            String[] times = timeLine.split(" --> ");
            String text = lines.get(i + 2);
            srtBo.setStartTime(times[0]);
            srtBo.setEndTime(times[1]);
            srtBo.setText(text);
            srtBo.setIndex(Integer.parseInt(index));
            srtBos.add(srtBo);
        }

        return srtBos;
    }


}