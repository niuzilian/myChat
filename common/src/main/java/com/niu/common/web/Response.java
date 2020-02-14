package com.niu.common.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * @program: myChat
 * @description:eb 结果封装
 * @author: niuzilian
 * @create: 2019-04-10 13:53
 **/
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response implements Serializable {
    private int code;
    public boolean isSuccess;
    private String msg;
    private Object data;
    private long time;

    public Response() {
    }

    public Response(int code, boolean isSuccess, String msg, Object data) {
        this.code = code;
        this.isSuccess = isSuccess;
        this.msg = msg;
        this.data = data;
        time = System.currentTimeMillis();
    }

    public static Response success() {
        return new Response(RestCode.SUCCESS.getCode(), true, RestCode.SUCCESS.getMsg(), null);
    }

    public static Response success(Object data) {
        return new Response(RestCode.SUCCESS.getCode(), true, RestCode.SUCCESS.getMsg(), data);
    }

    public static Response fail() {
        return new Response(RestCode.FAIL.getCode(), false, RestCode.FAIL.msg, null);
    }

    public static Response fail(String msg) {
        return new Response(RestCode.FAIL.getCode(), false, msg, null);
    }

    public static Response fail(RestCode restCode) {
        if (restCode.code == RestCode.SUCCESS.code) {
            throw new IllegalArgumentException("该方法不能创建成功的相应");
        }
        return new Response(restCode.code, false, restCode.msg, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", isSuccess=" + isSuccess +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", time=" + time +
                '}';
    }
}
