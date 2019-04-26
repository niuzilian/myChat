package com.niu.im.tcp;

import com.niu.common.constants.CmdEnum;
import com.niu.common.util.IntUtil;
import com.niu.im.status.MessageVerticle;
import com.niu.im.status.SocketSession;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.parsetools.RecordParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: myChat
 * @description:tcp服务
 * @author: niuzilian
 * @create: 2019-03-31 15:01
 **/
public class TcpServer extends AbstractVerticle {
    Logger logger = LoggerFactory.getLogger(TcpServer.class.getName());
    private EventBus eb;

    @Override
    public void start() throws Exception {
        super.start();
        eb = vertx.eventBus();
        Integer port = config().getInteger("tcp.port", 8082);
        Integer idleTimeout = config().getInteger("tcp.idleTimeout", 3600);
        NetServerOptions options = new NetServerOptions();
        options.setPort(port);
        //idleTimeout 时间内没有数据传输，关闭链接
        options.setIdleTimeout(idleTimeout);
        NetServer server = vertx.createNetServer(options);
        server.connectHandler(socket -> {
            //socket 主线ID
            String handlerID = socket.writeHandlerID();

            String remoteAddress = socket.remoteAddress().host();

            RecordParser parser = RecordParser.newFixed(8, null);

            parser.setOutput(new Handler<Buffer>() {
                int cmd = -1;
                int bodyLength = -1;

                @Override
                public void handle(Buffer buffer) {
                    if (bodyLength == -1) {
                        cmd = IntUtil.byteArr2Int(buffer.getBytes(0, 4));
                        if (CmdEnum.checkCmd(cmd)) {
                            bodyLength = IntUtil.byteArr2Int(buffer.getBytes(4, 8));
                            parser.fixedSizeMode(bodyLength);
                        } else {
                            bodyLength = -2;
                            logger.error("Illegal CMD code remote ip is:{}", remoteAddress);
                        }
                    } else if (bodyLength == -2) {
                        logger.error("Illegal CMD code remote ip is:{}, body is :{}", remoteAddress, buffer.toString());
                    } else {
                        try {
                            JsonObject msg = new JsonObject(buffer);
                            msg.put("cmd",cmd);
                            msg.put("handerId",handlerID);
                            dealMsg(msg);
                        } catch (Exception e) {
                            logger.error("Illegal message body body is :{}", buffer.toString());
                        }
                    }
                }
            });
            //读取数据
            socket.handler(parser);
            socket.exceptionHandler(v -> delSocket(handlerID));
            socket.closeHandler(v -> delSocket(handlerID));
        });
        server.listen(res -> {
            if (res.succeeded()) {
                logger.info("the TCP service listening was successful, and the listening port was:{}", server.actualPort());
            } else {
                logger.error("TCP service listening failure, e:{}", res.cause());
            }
        });
    }

    private void delSocket(String handlerId) {
        DeliveryOptions option = new DeliveryOptions();
        option.addHeader("action", SocketSession.Action.REMOVE_USER_SOCKET);
        option.setSendTimeout(3000);
        JsonObject param = new JsonObject();
        param.put("handlerId", handlerId);
        eb.send(SocketSession.class.getName(), param, res -> {
            if (res.succeeded()) {
                logger.info("remove handlerId  handlerId:{} result:{}", handlerId, res.result().body());
            } else {
                logger.info("remove handlerId fail  handlerId:{}  cause:{}", handlerId, res.cause());
            }
        });
    }

    private void dealMsg(JsonObject msg) {
        DeliveryOptions options = new DeliveryOptions();
        options.setSendTimeout(3000);
        options.addHeader("action", MessageVerticle.Action.DEAL_CLIENT_MSG);
        eb.send(MessageVerticle.class.getName(),msg,options);
    }
}