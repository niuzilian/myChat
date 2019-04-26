package com.niu.common.bean;

import com.niu.common.constants.CmdEnum;
import io.vertx.core.json.JsonObject;
import lombok.Data;

/**
 * @program: myChat
 * @description:
 * @author: niuzilian
 * @create: 2019-04-26 18:15
 **/
@Data
public class BaseEntity {
    private CmdEnum cmd;
    private JsonObject body;
}
