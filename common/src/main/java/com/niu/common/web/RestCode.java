package com.niu.common.web;

/**
 * @ClassName: RestCode
 * @Description: 统一定义错误码信息
 * @author: niuzilian
 * @date: 2019/11/8  8:26 PM
 */
public enum RestCode {
    SUCCESS(0,"成功"),

    FAIL(1,"操作失败"),

    PARAM_INVALID(400,"请求参数有误"),

    REQUEST_METHOD_ERROR(402,"请求方式错误"),

    NOT_FIND(404,"访问资源不存在"),

    SYSTEM_ERROR(500,"系统内部错误"),

    SIGN_ERROR(501,"签名错误"),

    REQUEST_HIGHT_FREQUENCY(600,"请求频率过高"),

    UNKNOWN_ERROR(999,"未知错误"),

    //================1000以下是系统级别的错误码===========

    LOGIN_FAILED(1111,"登录失败"),

    FRIENDS_ARE_NOT_ONLINE(1112,"好友未在线");



    int code;
    String msg;


    RestCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
