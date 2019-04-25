package com.niu.im.status;

import com.niu.common.bean.ChatBean;
import com.niu.common.bean.LoginBean;
import com.niu.common.constants.CmdEnum;
import com.niu.common.util.Result;
import com.niu.im.tcp.TcpServer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @program: myChat
 * @description:
 * @author: niuzilian
 * @create: 2019-04-15 14:52
 **/
public class MessageVerticle extends AbstractVerticle {
    Logger logger = LoggerFactory.getLogger(MessageVerticle.class.getName());

    private EventBus eb;

    public interface Action {
        String DEAL_CLIENT_MSG = "dealClientMsg";
    }

    @Override
    public void start() throws Exception {
        super.start();
        eb = vertx.eventBus();
        eb.<JsonObject>consumer(MessageVerticle.class.getName(), res -> {
            String action = res.headers().get("action");
            JsonObject body = res.body();
            switch (action) {
                case Action.DEAL_CLIENT_MSG:
                    dealMsg(body);
                    res.reply(0);
                    break;
                default:
                    break;
            }
        });
    }

    public void dealMsg(JsonObject msg) {
        Integer cmd = msg.getInteger("cmd");
        CmdEnum cmdEnum = CmdEnum.getCmdBycode(cmd);
        switch (cmdEnum) {
            case LOGIN:
                break;
            case LOGOUT:
                break;
            case HEARTBEAT:
                break;
            case SEND:
                break;
            default:
                break;
        }
    }


    private void setHandlerId(LoginBean loginBean) {
        JsonObject param = JsonObject.mapFrom(loginBean);
        Future.<Message<JsonObject>>future(logFut ->
                ebSend(param, SocketSession.Action.SET_USER_SOCKET, SocketSession.class.getName(), logFut)
        ).compose(sendFut -> {
            //TODO 发送给客户端回执 直接send HeadlerId 即可
           return  Future.future(v->{});
        });

    }

    private void ebSend(JsonObject param, String action, String adress, Handler<AsyncResult<Message<JsonObject>>> handler) {
        DeliveryOptions options = new DeliveryOptions();
        options.addHeader("action", action);
        options.setSendTimeout(3000);
        eb.send(adress, param, options, handler);
    }

    private void sendMsg(ChatBean chatMsg) {
        DeliveryOptions options = new DeliveryOptions();
        options.setSendTimeout(3000);
        options.addHeader("action", SocketSession.Action.GET_HANDLERID_BY_USERID);
        JsonObject query_param = new JsonObject();
        query_param.put("userId", chatMsg.getToId());
        eb.<JsonObject>send(SocketSession.class.getName(), query_param, res -> {
            if (res.succeeded()) {
                Result result = res.result().body().mapTo(Result.class);
                if (result.isSuccess) {
                    String handlerId = (String) result.getData();
                    eb.send(handlerId, JsonObject.mapFrom(chatMsg), sendRes -> {
                        if (sendRes.succeeded()) {
                            logger.info("send success " + JsonObject.mapFrom(chatMsg));
                        } else {
                            System.out.println("send fail " + JsonObject.mapFrom(chatMsg));
                        }
                    });
                } else {
                    System.out.println("get handle return fail " + result.getMsg());
                }
            } else {
                System.out.println("get handlerId fail" + res.cause());
            }
        });
    }
}