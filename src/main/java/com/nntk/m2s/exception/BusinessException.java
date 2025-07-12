package com.nntk.m2s.exception;

import com.nntk.m2s.constant.BaseRestCode;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class BusinessException extends RuntimeException  {

    private transient Object data;

    private String clientTip;

    private String message;

    private BaseRestCode restCode;

}
