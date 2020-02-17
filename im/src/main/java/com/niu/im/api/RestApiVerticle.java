package com.niu.im.api;

import com.niu.common.web.Response;
import com.niu.common.web.RestCode;
import com.niu.im.EventBusUtil;
import com.niu.im.service.IchatUserServiceVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: RestApiVerticle
 * @Description: 提供接口服务
 * @author: niuzilian
 * @date: 2020/2/15  12:52 PM
 */
public class RestApiVerticle extends AbstractVerticle {


    private final static Logger logger = LoggerFactory.getLogger(RestApiVerticle.class);


    private EventBus eventBus;

    @Override
    public void start() throws Exception {
        super.start();
        eventBus = vertx.eventBus();
        Integer httpPort = config().getInteger("im.http.port");
        //create httpServer
        HttpServer httpServer = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route().handler(CorsHandler.create("/api/v1/*").allowedMethod(HttpMethod.POST));

        //set bodyhandler  json upload
        router.route().handler(BodyHandler.create());
        //set failureHandler
        router.route().failureHandler(this::failureHandler);
        //accept json
        router.route("/api/v1/user/login").consumes("*/json").handler(this::loginHandler);
        httpServer.requestHandler(router::accept).listen(httpPort);
    }


    private void failureHandler(RoutingContext context) {
        HttpServerRequest request = context.request();
        if (context.statusCode() == 404) {
            logger.error("url is not fond; url={}, remoteAddress={}", request.uri(), request.remoteAddress().host());
            context.response().putHeader("content-type", "application/json")
                    .end(JsonObject.mapFrom(Response.fail(RestCode.NOT_FIND)).toString());
        } else {
            logger.error("rest error url={},statusCode={},errMst={}", request.uri(), context.statusCode(), context.response().getStatusMessage());
            context.response().putHeader("content-type", "application/json")
                    .end(JsonObject.mapFrom(Response.fail(RestCode.SYSTEM_ERROR)).toString());
        }
    }


    private void loginHandler(RoutingContext context) {
        JsonObject param = context.getBodyAsJson();
        String phone = param.getString("phone");
        String password = param.getString("password");
        if (StringUtils.isBlank(phone) || StringUtils.isBlank(password)) {
            fail(context, RestCode.PARAM_INVALID);
            return;
        }
        DeliveryOptions deliveryOptions = EventBusUtil.getDeliveryOptions(IchatUserServiceVerticle.Action.LOGIN);
        eventBus.<JsonObject>send(IchatUserServiceVerticle.class.getName(), param, deliveryOptions,res -> {
            if (res.succeeded()) {
                JsonObject body = res.result().body();
                logger.info("Login results ; phone={},password={},body={}", phone, password, body.toString());
                success(context, body);
            } else {
                logger.error("login error;phone={},password={},e={}", phone, password, res.cause());
                fail(context, RestCode.LOGIN_FAILED);
            }
        });
        //RestCode.WRONG_NAME_OR_PASSWORD)
    }

    public void success(RoutingContext context, JsonObject result) {
        context.response().putHeader("content-type", "application/json").end(result.toString());
    }

    public void fail(RoutingContext context, RestCode restCode) {
        logger.error("rest failed ；uri={}, param={}, errorCode={},errorMsg={}"
                , context.request().uri(), context.getBodyAsJson(), restCode.getCode(), restCode.getMsg());
        context.response().putHeader("content-type", "application/json").end(JsonObject.mapFrom(Response.fail(restCode)).toString());
    }


}
