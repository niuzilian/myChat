package com.niu.common.bean;

import lombok.Data;

/**
 * @program: myChat
 * @description:
 * @author: niuzilian
 * @create: 2019-04-24 18:59
 **/
@Data
public class LoginBean {
    private Integer cmd;
    private Integer userId;
    private String handler;
}
