package com.niu.im.service;

import com.niu.common.web.Response;
import com.niu.common.web.RestCode;
import com.niu.im.EventBusUtil;
import com.niu.im.dao.IchatUserDaoVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: IchatUserServiceVerticle
 * @Description: ichatUser 服务
 * @author: niuzilian
 * @date: 2020/2/17  1:09 PM
 */
public class IchatUserServiceVerticle extends AbstractVerticle {
    private final static Logger logger = LoggerFactory.getLogger(IchatUserServiceVerticle.class);
    private EventBus eventBus;

    public interface Action {
        String LOGIN = "login";
    }

    @Override
    public void start() throws Exception {
        super.start();
        eventBus = vertx.eventBus();
        eventBus.<JsonObject>consumer(IchatUserServiceVerticle.class.getName(), res -> {
            String action = res.headers().get("action");
            JsonObject body = res.body();
            switch (action) {
                case Action.LOGIN:
                    login(body, loginRes -> {
                        if (loginRes.succeeded()) {
                            res.reply(JsonObject.mapFrom(loginRes.result()));
                        } else {
                            res.reply(JsonObject.mapFrom(Response.fail(loginRes.cause().getMessage())));
                        }
                    });
                    break;
                default:
                    break;

            }


        });

    }


    private void login(JsonObject user, Handler<AsyncResult<Response>> handler) {
        eventBus.<JsonObject>send(IchatUserDaoVerticle.class.getName(), user,EventBusUtil.getDeliveryOptions(IchatUserDaoVerticle.Action.SELECT_BY_PHONE), res -> {
            if (res.succeeded()) {
                JsonObject body = res.result().body();
                Response response = body.mapTo(Response.class);
                if (response.isSuccess) {
                    if (response.getData() == null) {
                        handler.handle(Future.succeededFuture(Response.fail(RestCode.USER_DOES_NOT_EXIST)));
                    } else {
                        JsonObject userDB = JsonObject.mapFrom(response.getData());
                        if (user.getString("password").equals(userDB.getString("password"))) {
                            handler.handle(Future.succeededFuture(Response.success(userDB)));
                        } else {
                            handler.handle(Future.succeededFuture(Response.fail(RestCode.PASSWORD_ERROR)));
                        }
                    }
                } else {
                    handler.handle(Future.succeededFuture(response));
                }
            } else {
                logger.error("call ichatUserDaoVerticle error e={}", res.cause());
                handler.handle(Future.failedFuture(res.cause()));
            }
        });
    }
}
