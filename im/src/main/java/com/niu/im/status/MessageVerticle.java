package com.niu.im.status;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.niu.common.bean.BaseEntity;
import com.niu.common.bean.ChatEntity;
import com.niu.common.bean.LoginEntity;
import com.niu.common.constants.CmdEnum;
import com.niu.common.util.Result;
import com.niu.common.util.TrasfBufferUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;


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
                LoginEntity loginEntity = new LoginEntity();
                loginEntity.setHandler(msg.getString("handlerId"));
                loginEntity.setUserId(msg.getInteger("userId"));
                dologin(loginEntity);
                break;
            case LOGOUT:
                break;
            case HEARTBEAT:
                break;
            case SEND:
                ChatEntity chat = new ChatEntity();
                chat.setFromId(msg.getInteger("fromId"));
                chat.setToId(msg.getInteger("toId"));
                chat.setContent(msg.getString("content"));
                sendMsg(chat);
                break;
            default:
                break;
        }
    }

    private void dologin(LoginEntity loginBean) {
        JsonObject param = JsonObject.mapFrom(loginBean);
        Future.<Message<Result>>future(logFut ->
                ebSend(param, SocketSession.Action.SET_USER_SOCKET, SocketSession.class.getName(), logFut)
        ).compose(res -> {
            if (res.body().isSuccess) {
                BaseEntity baseEntity = new BaseEntity();
                baseEntity.setCmd(CmdEnum.LOGIN_ACK);
                Buffer bf = TrasfBufferUtil.toBuff(baseEntity);
                logger.debug("send login_ack to client userId:{},handlerId:{}", loginBean.getUserId(), loginBean.getHandler());
                return Future.future(v -> eb.send(loginBean.getHandler(), bf));
            } else {
                logger.error("save handlerId fail userId:{} handlerId:{} failMsg:{}", loginBean.getUserId(), loginBean.getHandler(), res.body().getMsg());
                return Future.future();
            }
        }).setHandler(res -> {
            if (res.succeeded()) {
                logger.debug("login success userId:{}", loginBean.getUserId());
            } else {
                logger.error("login success userId:{}", loginBean.getUserId());
            }

        });
    }

    private void sendMsg(ChatEntity chatMsg) {
        Future.<Message<Result>>future(f -> {
            JsonObject query_param = new JsonObject();
            query_param.put("userId", chatMsg.getToId());
            ebSend(query_param, SocketSession.Action.GET_HANDLERID_BY_USERID, Socket.class.getName(), f);
        }).compose(queryRes -> {
            Result result = queryRes.body();
            if (result.isSuccess) {
                String handlerId = (String) result.getData();
                BaseEntity baseEntity = new BaseEntity();
                baseEntity.setCmd(CmdEnum.SEND);
                baseEntity.setBody(JsonObject.mapFrom(chatMsg));
                logger.debug("userId={} send a msg to userId={}", chatMsg.getFromId(), chatMsg.getToId());
                return Future.future(f -> eb.send(handlerId, TrasfBufferUtil.toBuff(baseEntity)));
            } else {
                logger.error("send msg is fail chatMsg=" + Json.encode(chatMsg) + " failMsg=" + result.getMsg());
                return Future.future();
            }
        }).setHandler(res -> {
            if (res.succeeded()) {
                logger.debug("chat msg send success chatMsg:{}", Json.encode(chatMsg));
            } else {
                logger.error("chat msg send fail chatMsg:{}", Json.encode(chatMsg));
            }

        });
    }

    private void ebSend(JsonObject param, String action, String adress, Handler<AsyncResult<Message<Result>>> handler) {
        DeliveryOptions options = new DeliveryOptions();
        options.addHeader("action", action);
        options.setSendTimeout(3000);
        eb.send(adress, param, options, handler);
    }
}