package com.nntk.m2s.exception;

import com.nntk.m2s.constant.BaseRestCode;

public class ExceptionFactory {

    public static void createBusiness(BaseRestCode restCode, String clientInfo) {
        throw BusinessException
                .builder()
                .restCode(restCode)
                .clientTip(clientInfo)
                .build();
    }

}
