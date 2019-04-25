package com.niu.common.util;

import com.niu.common.constants.CodeEnum;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

import java.io.Serializable;

/**
 * @program: myChat
 * @description:eb 结果封装
 * @author: niuzilian
 * @create: 2019-04-10 13:53
 **/
public class Result implements Serializable {
    private int code;
    public boolean isSuccess;
    private String msg;
    private Object data;
    private long time;

    public Result() {
    }

    public Result(int code, boolean isSuccess, String msg, Object data) {
        this.code = code;
        this.isSuccess = isSuccess;
        this.msg = msg;
        this.data = data;
        time = System.currentTimeMillis();
    }

    public static Result success(Object data) {
        return new Result(CodeEnum.SUCCESS.getCode(), true, CodeEnum.SUCCESS.getMsg(), data);
    }

    public static Result fail(String msg) {
        return new Result(CodeEnum.FAIL.getCode(), false, msg, null);
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


}
