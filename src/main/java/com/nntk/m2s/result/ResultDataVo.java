
package com.nntk.m2s.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultDataVo<T> {

    /**
     * 业务状态码
     */
    private int code;
    /**
     * 成功详情，展示给前端调用者
     */
    private String message;
    /**
     * 数据
     */
    private T data;

    /**
     * 页面显示，展示给用户，如果前端不显示具体提示，可以直接饮用后台的这个字段
     */
    private String clientTip;

    /**
     * 这三个可不用
     */
    private String traceId;
    private Date timestamp;
    private String path;


}
