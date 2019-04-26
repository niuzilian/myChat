package com.niu.common.bean;

import lombok.Data;

/**
 * @program: myChat
 * @description:
 * @author: niuzilian
 * @create: 2019-03-31 16:14
 **/
@Data
public class ChatEntity {
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
