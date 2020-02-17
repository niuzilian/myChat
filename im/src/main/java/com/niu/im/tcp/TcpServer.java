package com.niu.im.tcp;

import com.niu.common.bean.ChatEntity;
import com.niu.common.constants.CmdEnum;
import com.niu.common.util.IntUtil;
import com.niu.common.web.Response;
import com.niu.common.web.RestCode;
import com.niu.im.EventBusUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.SocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program:myChat
 * @description:tcp服务
 * @author:niuzilian
 * @create:2019-03-31 15:01
 **/
public class TcpServer extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(TcpServer.class.getName());
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

            SocketAddress socketAddress = socket.remoteAddress();

            logger.info("get connection remote address={},port={},handlerId={}", socketAddress.host(), socketAddress.port(), handlerID);

            socket.handler(buffer -> {
                int cmd = IntUtil.byteArr2Int(buffer.getBytes(0, 4));
                if (CmdEnum.checkCmd(cmd)) {
                    int bodyLength = IntUtil.byteArr2Int(buffer.getBytes(4, 8));
                    JsonObject body = buffer.getBuffer(8, 8 + bodyLength).toJsonObject();
                    body.put("cmd", cmd);
                    body.put("handlerId", handlerID);
                    dealMsg(body);
                } else {
                    //TODO cmd错误 记录错误次数，如果超过了错误次数 关闭该链接，也可以将此ip加入黑名单
                }
            });

            socket.exceptionHandler(v -> {
                logger.info("Socket is closed abnormally; handlerId={},remontIp={},port={}", handlerID, socketAddress.host(), socketAddress.port());
                //执行登出
                this.delSocket(handlerID);

            });
            socket.closeHandler(v -> {
                logger.info("Socket is closed; handlerId={},remontIp={},port={}", handlerID, socketAddress.host(), socketAddress.port());
                //执行登出
                this.delSocket(handlerID);
            });
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
        JsonObject param = new JsonObject();
        param.put("handlerId", handlerId);
        eb.send(SocketSession.class.getName(),param,EventBusUtil.getDeliveryOptions(SocketSession.Action.REMOVE_USER_SOCKET),res->{
            logger.info("socket close remove session  handlerId:{} result:{}", handlerId, res.result());
        });
    }


    public void dealMsg(JsonObject msg) {
        Integer cmd = msg.getInteger("cmd");
        CmdEnum cmdEnum = CmdEnum.getCmdBycode(cmd);
        if (cmdEnum != null) {
            switch (cmdEnum) {
                case LOGIN:
                    logger.info("socket login param={}", msg.toString());
                    eb.<JsonObject>send(SocketSession.class.getName(),msg,EventBusUtil.getDeliveryOptions(SocketSession.Action.SET_USER_SOCKET),res->{
                        JsonObject body = res.result().body();
                        logger.info("socket login param={},result={}", msg.toString(), body.toString());
                        eb.send(msg.getString("handlerId"), Json.encodeToBuffer(body));
                    });
                    break;
                case LOGOUT:
                    logger.info("socket loginout param={}", msg.toString());
                    eb.<JsonObject>send(SocketSession.class.getName(),msg,EventBusUtil.getDeliveryOptions(SocketSession.Action.REMOVE_USER_SOCKET),res->{
                        JsonObject body = res.result().body();
                        logger.info("socket loginout param={},result={}", msg.toString(), body.toString());
                        eb.send(msg.getString("handlerId"), Json.encodeToBuffer(body));
                    });
                    break;
                case HEARTBEAT:
                    logger.info("Heartbeat Report msg={}", msg.toString());
                    eb.<JsonObject>send(msg.getString("handlerId"), Json.encodeToBuffer(Response.success()));
                    break;
                case SEND:
                    Integer fromId = msg.getInteger("fromId");
                    Integer toId = msg.getInteger("toId");
                    String content = msg.getString("content");
                    String fromHandlerId = msg.getString("handlerId");
                    logger.info("Chat message msg={}", msg.toString());
                    eb.<JsonObject>send(SocketSession.class.getName(),new JsonObject().put("userId", toId),EventBusUtil.getDeliveryOptions(SocketSession.Action.GET_HANDLERID_BY_USERID),res->{
                        if (res.failed()) {
                            eb.send(fromHandlerId, Json.encodeToBuffer(Response.fail(RestCode.SYSTEM_ERROR)));
                        } else {
                            JsonObject body = res.result().body();
                            Response response = body.mapTo(Response.class);
                            if (response.isSuccess) {
                                String toHandlerId = response.getData().toString();
                                ChatEntity chatEntity = new ChatEntity();
                                chatEntity.setFromId(fromId);
                                chatEntity.setToId(toId);
                                chatEntity.setContent(content);
                                eb.send(toHandlerId, Json.encodeToBuffer(chatEntity));
                                //TODO 需要消息回执确认,暂时先采用不可靠的传送方式
                                eb.send(fromHandlerId, Json.encodeToBuffer(Response.success()));
                            } else {
                                eb.send(fromHandlerId, Json.encodeToBuffer(Response.fail(RestCode.FRIENDS_ARE_NOT_ONLINE)));
                            }
                        }
                    });
                    break;
                default:
                    break;
            }
        } else {
            logger.error("cmd is error; cmd=" + cmd);
        }

    }

}
