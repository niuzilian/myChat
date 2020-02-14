package com.niu.im.tcp;

import com.niu.common.constants.CmdEnum;
import com.niu.common.util.ByteBuffer;
import com.niu.common.util.IntUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * @program: myChat
 * @description:
 * @author: niuzilian
 * @create: 2019-03-31 16:40
 **/
public class TcpClient extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(TcpClient.class);


    @Override
    public void start() throws Exception {
        super.start();
        NetClientOptions options = new NetClientOptions();
        options.setConnectTimeout(1000).setReconnectAttempts(10).setReconnectInterval(3000);
        NetClient netClient = vertx.createNetClient();
        netClient.connect(8082, "localhost", socket -> {
            if (socket.succeeded()) {
                logger.info("tcp 1 客户端链接成功");
                NetSocket netSocket = socket.result();

                new Thread(() -> {
                    Scanner scanner = new Scanner(System.in);
                    while (true) {
                        System.out.println("请输入命令 登录-login；登出-loginOut;聊天-chat");
                        String text = scanner.nextLine();
                        if (StringUtils.isNotBlank(text)) {
                            switch (text) {
                                case "login":
                                    System.out.println("请输入要登录的用户id");
                                    String userIdStr = scanner.nextLine();
                                    if (StringUtils.isNotBlank(userIdStr)) {
                                        int userId = Integer.parseInt(userIdStr);
                                        JsonObject msgBody = new JsonObject().put("userId", userId);
                                        netSocket.write(buildMsg(CmdEnum.LOGIN, msgBody));
                                    } else {
                                        System.out.println("用户id为空");
                                    }
                                    break;
                                case "loginOut":
                                    System.out.println("输入登出的id");
                                    String next = scanner.nextLine();
                                    if (StringUtils.isNotBlank(next)) {
                                        int userId = Integer.parseInt(next);
                                        JsonObject msgBody = new JsonObject().put("userId", userId);
                                        netSocket.write(buildMsg(CmdEnum.LOGOUT, msgBody));
                                    } else {
                                        System.out.println("用户id为空");
                                    }
                                    break;
                                default:
                                    System.out.println("输入你自己的id");
                                    String s = scanner.nextLine();
                                    if (StringUtils.isNotBlank(s)) {
                                        int fromId = Integer.parseInt(s);
                                        System.out.println("输入对方的ID");
                                        String s1 = scanner.nextLine();
                                        int toId = Integer.parseInt(s1);
                                        System.out.println("输入聊天内容");
                                        String s2 = scanner.nextLine();
                                        JsonObject msg = new JsonObject();
                                        msg.put("fromId", fromId).put("toId", toId).put("content", s2);
                                        netSocket.write(buildMsg(CmdEnum.SEND, msg));
                                    }
                                    break;
                            }
                        }
                    }
                }).start();

                netSocket.handler(buffer -> {
                    System.out.println("====收到客户端消息=====" + buffer.toString());
                });

            } else {
                logger.info("链接失败 " + socket.cause());

            }
        });
    }


    private  Buffer buildMsg(CmdEnum cmdEnum, JsonObject msg) {
        ByteBuffer bodyBuffer = new ByteBuffer();
        byte[] cmd = IntUtil.int2ByteArr(cmdEnum.getCode());
        byte[] bodyArray = msg.toBuffer().getBytes();
        byte[] bodyLength = IntUtil.int2ByteArr(bodyArray.length);
        byte[] msgByteArray = bodyBuffer.append(cmd).append(bodyLength).append(bodyArray).toBytes();
        return Buffer.buffer(msgByteArray);
    }

}
