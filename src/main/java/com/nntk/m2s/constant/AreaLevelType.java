package com.nntk.m2s.constant;

import lombok.Getter;

@Getter

public enum AreaLevelType {
    PROVINCE(1, "省"),
    CITY(2, "城市"),
    DISTINCT(3, "区"),
    COUNTRY(4, "国家"),
    ;


    private Integer code;

    private String msg;

    AreaLevelType(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
