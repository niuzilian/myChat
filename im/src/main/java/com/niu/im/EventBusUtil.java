package com.niu.im;

import com.niu.common.web.Response;
import io.vertx.core.Future;
import io.vertx.core.Handler;
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

    public static DeliveryOptions getDeliveryOptions(String action){
        DeliveryOptions options = new DeliveryOptions();
        options.addHeader("action", action);
        options.setSendTimeout(3000);
        return options;
    }
}
