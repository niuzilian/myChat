package com.niu.im;

import com.niu.common.web.Response;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * @ClassName: EventBusUtil
 * @Description: 向verticle中发送数据，并封装返回结果
 * @author: niuzilian
 * @date: 2020/2/14  4:47 PM
 */
public final class EventBusUtil {

    public static Future<Response> ebSend(JsonObject param, String adress, String action, EventBus eventBus) {
        Future<Message<JsonObject>> future = Future.future();
        Future<Response> responseFuture = Future.future();
        DeliveryOptions options = new DeliveryOptions();
        options.addHeader("action", action);
        options.setSendTimeout(3000);
        eventBus.send(adress, param, options, future.completer());
        future.setHandler(res -> {
            if (res.succeeded()) {
                JsonObject body = res.result().body();
                responseFuture.complete(body.mapTo(Response.class));
            } else {
                responseFuture.fail(res.cause());
            }
        });
        return responseFuture;
    }
}
