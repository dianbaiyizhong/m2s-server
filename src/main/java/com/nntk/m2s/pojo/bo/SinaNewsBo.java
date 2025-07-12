package com.nntk.m2s.pojo.bo;

import cn.hutool.core.util.ArrayUtil;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SinaNewsBo {


    private String keywords;
    private String urls;
    private String title;
    private String mediaName;
    private Long intime;

    private LocalDateTime newsTime;


    private String sourceUrl;

    private String sinaRawImages;

    private String thumbUrl;

    private String images;


    private Integer posInfoId;


    private String content;

    private String rawContent;
    private int areaLevel;

    public String[] getKewordsArray() {
        return ArrayUtil.reverse(keywords.split(","));
    }


}
