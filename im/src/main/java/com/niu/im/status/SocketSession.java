package com.niu.im.status;

import com.niu.common.util.Result;
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
    private static final Logger logger= LoggerFactory.getLogger(SocketSession.class);
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
                    res.reply(JsonObject.mapFrom(Result.fail("unsupported action")));
                    break;
            }
        });
    }

    private Result setUserSocket(Integer userId, String handlerId) {
        this.sessionMap.put(userId, handlerId);
        this.sessionReverse.put(handlerId, userId);
        counter++;
        recounter++;
        logger.info("set userSocket success userId="+userId+" handlerId="+handlerId+" counter="+counter+" recounter="+recounter);
        return Result.success(null);
    }

    private Result removeSocket(Integer userId, String handlerId) {
        if (userId != null) {
            //login out
            this.sessionMap.remove(userId);
            this.sessionReverse.remove(handlerId);
            logger.info("login out userId="+userId+"handlerId="+handlerId);
        } else {
            //socket close
            userId = this.sessionReverse.get(handlerId);
            sessionMap.remove(userId);
            logger.info("socket close userId="+userId+"handlerId="+handlerId);
        }
        counter--;
        counter--;
        return Result.success(null);
    }

    private Result getHandlerIdByUserId(Integer userId) {
        String handlerId = this.sessionMap.get(userId);
        if (handlerId != null) {
            return Result.success(handlerId);
        } else {
            return Result.fail("handlerId does not exist");
        }
    }

    private Result getUserIdByHandlerId(String handlerId) {
        Integer userId = this.sessionReverse.get(handlerId);
        if (userId != null) {
            return Result.success(userId);
        } else {
            return Result.fail("userId does not exist");
        }
    }
}
