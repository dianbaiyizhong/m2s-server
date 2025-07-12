package com.nntk.m2s.constant;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BaseRestCode {

    public BaseRestCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public BaseRestCode(int code) {
        this.code = code;
    }

    private int code;
    private String message;
    private String clientInfo;
}
