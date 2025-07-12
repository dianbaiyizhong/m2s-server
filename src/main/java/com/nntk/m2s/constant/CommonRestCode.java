package com.nntk.m2s.constant;

public class CommonRestCode extends BaseRestCode {
    public static final CommonRestCode SUCCESS = new CommonRestCode(0, "success");
    public static final CommonRestCode PARAM_ERROR = new CommonRestCode(400001, "参数错误");
    public static final CommonRestCode BUSINESS_ERROR = new CommonRestCode(200000);
    public static final CommonRestCode LOGIN_ERROR = new CommonRestCode(200001, "登录失败");
    public static final CommonRestCode UN_KNOW_ERROR = new CommonRestCode(500000, "未知错误");
    public static final CommonRestCode CODE_409001 = new CommonRestCode(409001);
    public static final CommonRestCode ALREADY_GET = new CommonRestCode(409001, "不允许重复执行操作");
    public static final CommonRestCode NOT_FOUND_RECORD = new CommonRestCode(404001, "查无此记录...");
    public static final CommonRestCode ALREADY_GET_TICKET = new CommonRestCode(409001, "不允许重复执行操作", "你已抢到该站火车票");
    public static CommonRestCode ACCESS_DENIED = new CommonRestCode(403000, "权限不足");
    public static CommonRestCode NOT_LOGIN = new CommonRestCode(401000, "未登录");


    public CommonRestCode(int code, String message, String clientInfo) {
        super(code, message, clientInfo);
    }

    public CommonRestCode(int code, String message) {
        super(code, message);
    }

    public CommonRestCode(int code) {
        super(code);
    }
}
