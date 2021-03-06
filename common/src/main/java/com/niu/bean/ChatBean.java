package com.niu.bean;

import lombok.Data;

/**
 * @program: myChat
 * @description:
 * @author: niuzilian
 * @create: 2019-03-31 16:14
 **/
@Data
public class ChatBean {
    /**
     * 发起方
     */
    private Integer fromId;
    /**
     * 接受方
     */
    private Integer toId;
    /**
     * 发送内容
     */
    private String content;

}
