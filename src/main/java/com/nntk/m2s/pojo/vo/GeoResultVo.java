package com.nntk.m2s.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeoResultVo {


    private int id;
    private String name;
    private String code;
    private double longitude;
    private double latitude;
}
