package com.niu.page.socket;

import com.niu.common.util.IntUtil;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

/**
 * @program: myChat
 * @description:
 * @author: niuzilian
 * @create: 2019-04-24 15:28
 **/
public class RecordPaseTest {
    public static void main(String[] args) {
        RecordParser parser = RecordParser.newFixed(12,null);
        Handler<Buffer> handler =new Handler<Buffer>() {
            int cmd=-1;
            int headLength=0;
            int bodyLength=-1;
            @Override
            public void handle(Buffer buffer) {
                if(bodyLength==-1){
                    cmd= IntUtil.byteArr2Int(buffer.getBytes(0,4));
                    headLength= IntUtil.byteArr2Int(buffer.getBytes(4,8));
                    bodyLength= IntUtil.byteArr2Int(buffer.getBytes(8,12));
                    int a= 1/0;
                    if(bodyLength==0){

                        //hartbeat
                        System.out.println("这是一个心跳");
                    }else{
                        parser.fixedSizeMode(bodyLength+headLength);
                    }

                }else{
                    if(headLength!=0){
                        System.out.println("cmd="+cmd);
                        System.out.println("headLength="+headLength);
                        System.out.println("bodyLength="+bodyLength);

                        String content=buffer.toString();
                        System.out.println();
                        System.out.println(content.substring(0, headLength));
                        System.out.println(content.substring(headLength, bodyLength+headLength));

                        //   System.out.println("消息头"+ (Buffer.buffer(buffer.getBytes(0,headLength).toString())));
                        //   System.out.println("消息体："+(Buffer.buffer(buffer.getBytes(headLength,bodyLength+headLength).toString())));
                    }
                }
            }
        };
        parser.setOutput(handler);

        int cmd = 0;
        int headLength = 2;
        int bodyLength= 4;


        byte[] cmdByte = IntUtil.int2ByteArr(cmd);
        byte[] headByte = IntUtil.int2ByteArr(headLength);
        byte[] bodyByte = IntUtil.int2ByteArr(bodyLength);

        byte[] head = "aa".getBytes();
        byte[] body = "bbbb".getBytes();


        byte[] send = new byte[cmdByte.length+headByte.length+bodyByte.length+head.length+body.length];

        int lenth=0;
        System.arraycopy(cmdByte,0,send,lenth,cmdByte.length);
        lenth=lenth+cmdByte.length;
        System.arraycopy(headByte,0,send,lenth,headByte.length);
        lenth=lenth+headByte.length;
        System.arraycopy(bodyByte,0,send,lenth,bodyByte.length);
        lenth=lenth+bodyByte.length;
        System.arraycopy(head,0,send,lenth,head.length);
        lenth=lenth+head.length;
        System.arraycopy(body,0,send,lenth,body.length);

        parser.handle(Buffer.buffer(send));
    }
}
