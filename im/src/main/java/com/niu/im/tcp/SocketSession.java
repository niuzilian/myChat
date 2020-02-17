package com.niu.im.tcp;

import com.niu.common.web.Response;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: myChat
 * @description:记录所有链接状态
 * @author: niuzilian
 * @create: 2019-04-10 13:39
 **/
public class SocketSession extends AbstractVerticle {
    private final Logger logger = LoggerFactory.getLogger(SocketSession.class);
    private EventBus eb;
    private Map<Integer, String> sessionMap = new HashMap();
    private Map<String, Integer> sessionReverse = new HashMap<>();
    private int counter = 0;
    private int recounter = 0;


    public interface Action {
        String SET_USER_SOCKET = "setUserSocket";
        String REMOVE_USER_SOCKET = "removeUserSocket";
        String GET_HANDLERID_BY_USERID = "getHandlerIdByUserId";
        String GET_USERID_BY_HANDLERID = "getUserIdByHandlerId";
    }

    @Override
    public void start() throws Exception {
        super.start();
        eb = vertx.eventBus();
        eb.<JsonObject>consumer(SocketSession.class.getName(), res -> {
            String action = res.headers().get("action");
            JsonObject body = res.body();
            Integer uid = body.getInteger("userId");
            String handlerId = body.getString("handlerId");
            logger.debug("socket session received msg body is {}", body);
            switch (action) {
                case Action.SET_USER_SOCKET:
                    res.reply(JsonObject.mapFrom(setUserSocket(uid, handlerId)));
                    break;
                case Action.REMOVE_USER_SOCKET:
                    res.reply(JsonObject.mapFrom(removeSocket(uid, handlerId)));
                    break;
                case Action.GET_HANDLERID_BY_USERID:
                    res.reply(JsonObject.mapFrom(getHandlerIdByUserId(uid)));
                    break;
                case Action.GET_USERID_BY_HANDLERID:
                    res.reply(JsonObject.mapFrom(getUserIdByHandlerId(handlerId)));
                    break;
                default:
                    res.reply(JsonObject.mapFrom(Response.fail("unsupported action")));
                    break;
            }
        });
    }

    private Response setUserSocket(Integer userId, String handlerId) {
        this.sessionMap.put(userId, handlerId);
        this.sessionReverse.put(handlerId, userId);
        counter++;
        recounter++;
        logger.debug("set userSocket success userId={},handleId={},counter={},recounter={},sessionMap.size={},sessionReverse.size={}",
                userId, handlerId, counter, recounter, this.sessionMap.size(), this.sessionReverse.size());
        return Response.success();
    }

    private Response removeSocket(Integer userId, String handlerId) {
        if (userId != null) {
            //login out
            this.sessionMap.remove(userId);
            this.sessionReverse.remove(handlerId);
            logger.info("login out userId={},handlerId={}", userId, handlerId);
        } else {
            //socket close
            userId = this.sessionReverse.get(handlerId);
            if (userId != null) {
                this.sessionMap.remove(userId);
            }
            this.sessionReverse.remove(handlerId);
            logger.info("socket close userId={},handlerId={}", userId, handlerId);
        }
        counter--;
        recounter--;
        return Response.success();
    }

    private Response getHandlerIdByUserId(Integer userId) {
        String handlerId = this.sessionMap.get(userId);
        if (handlerId != null) {
            return Response.success(handlerId);
        } else {
            return Response.fail("handlerId does not exist");
        }
    }

    private Response getUserIdByHandlerId(String handlerId) {
        Integer userId = this.sessionReverse.get(handlerId);
        if (userId != null) {
            return Response.success(userId);
        } else {
            return Response.fail("userId does not exist");
        }
    }
}
