package com.nntk.m2s.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewsVo implements Serializable {


    private int areaLevel;
    private String intro;
    private Double lat;
    private Double lng;
    private int areaId;
    private String name;

    private String code;

    private String title;

    private String themeColor;

    private String provinceShortHand;

    private String licensePlateNum;


    private String ncpSlogan;

    private Long articleTime;

    private String sourceUrl;
    private String sourceName;

    private String thumbImg;


    private String locationSub;


    private String videoUrl;


    private String content;


    private String areaImage;
}
