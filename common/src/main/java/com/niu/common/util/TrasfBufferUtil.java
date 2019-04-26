package com.niu.common.util;

import com.niu.common.bean.BaseEntity;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.io.UnsupportedEncodingException;


/**
 * @program: myChat
 * @description:
 * @author: niuzilian
 * @create: 2019-04-26 18:21
 **/
public class TrasfBufferUtil {
   private final static Logger logger= LoggerFactory.getLogger(TrasfBufferUtil.class);

    public static Buffer toBuff (BaseEntity baseEntity){
        try {
            Buffer buff = Buffer.buffer();
            byte[] cmdByte = IntUtil.int2ByteArr(baseEntity.getCmd().getCode());
            buff.appendBytes(cmdByte);
            JsonObject body = baseEntity.getBody();
            if(body==null){
               buff.appendBytes(IntUtil.int2ByteArr(0));
            }else{
                byte[] bodyByte =body.encode().getBytes("UTF-8");
                buff.appendBytes(IntUtil.int2ByteArr(bodyByte.length));
                buff.appendBytes(bodyByte);
            }
           return buff;
        } catch (UnsupportedEncodingException e) {
            logger.error("Failure to convert message to buffer cmd:{},body:{}",baseEntity.getCmd().getCode(),baseEntity);
            return Buffer.buffer();
        }
    }
}
