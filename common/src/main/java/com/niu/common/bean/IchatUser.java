package com.niu.common.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName: IchatUser
 * @Description: 用户的基本信息表
 * @author: niuzilian
 * @date: 2020/2/16  3:29 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class IchatUser {

    private Integer id;

    private String nickname;

    private String password;

    private Byte gender;

    private String phone;

    private String ichatNo;

    private Date createTime;

    private Date updateTime;

}
