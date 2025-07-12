package com.nntk.m2s.result;

import com.nntk.m2s.constant.BaseRestCode;
import com.nntk.m2s.constant.CommonRestCode;
import com.nntk.m2s.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Date;
import java.util.Optional;

/**
 * http body的data的构造类
 */
@Slf4j
public class RespBodyBuilder {
    private static final String B3_TRACE_ID = "X-B3-TraceId";

    private RespBodyBuilder() {
    }


    public static ResultDataVo build(int code, String message, Object data) {

        return ResultDataVo.builder().code(code).message(message).data(data).build();

    }

    public static ResultDataVo build(int code, String message) {

        return ResultDataVo.builder().code(code).message(message).build();

    }


    public static ResultDataVo build(BaseRestCode restCode, Object data) {

        return ResultDataVo.builder().code(restCode.getCode()).message(restCode.getMessage()).data(data).build();

    }

    public static ResultDataVo buildSuccess(String clientTip) {

        return ResultDataVo.builder().code(CommonRestCode.SUCCESS.getCode()).message(CommonRestCode.SUCCESS.getMessage()).clientTip(clientTip).build();

    }

    /**
     * 返回一个简单的成功对象，一般是更新，修改成功等操作
     *
     * @return
     */
    public static ResultDataVo success() {
        return ResultDataVo.builder().code(CommonRestCode.SUCCESS.getCode()).message(CommonRestCode.SUCCESS.getMessage()).build();
    }

    /**
     * 返回一个实体类
     *
     * @param data
     * @return
     */
    public static ResultDataVo success(Object data) {

        return ResultDataVo.builder().code(CommonRestCode.SUCCESS.getCode()).message(CommonRestCode.SUCCESS.getMessage()).data(data).build();

    }

    public static ResultDataVo create(BusinessException businessException) {
        String clientInfo = Optional.ofNullable(businessException.getRestCode().getClientInfo()).orElse(businessException.getClientTip());
        String message = Optional.ofNullable(businessException.getRestCode().getMessage()).orElse(businessException.getMessage());
        Integer code = businessException.getRestCode().getCode();
        return ResultDataVo.builder()
                .code(code)
                .message(message)
                .clientTip(clientInfo)
                .build();
    }

    /**
     * 返回一个错误的实体类
     *
     * @param exception
     * @param request
     * @param code
     * @param message
     * @return
     */
    public static ResultDataVo error(Exception exception, HttpServletRequest request, int code, String message) {
        log.error("[Error occurred.] - [code={}, message={}] ", code, exception.getMessage());
        log.error("print stack:{}", getStackTraceByPn(exception, "com.zhenmei.wsc"));

        return ResultDataVo.builder()
                .code(code)
                .message(message)
                .data(exception.getMessage())
                .traceId(MDC.get(B3_TRACE_ID))
                .timestamp(new Date())
                .path(request.getRequestURI())
                .build();
    }


    public static String getStackTraceByPn(Throwable e, String packagePrefix) {
        StringBuilder s = new StringBuilder("\n").append(e);
        for (StackTraceElement traceElement : e.getStackTrace()) {
            if (!traceElement.getClassName().startsWith(packagePrefix)) {
                break;
            }
            s.append("\n\tat ").append(traceElement);
        }
        return s.toString();
    }


}
