package com.niu.im.dao;

import com.niu.common.bean.IchatUser;
import com.niu.common.web.Response;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @ClassName: IchatUserDaoVerticle
 * @Description: 用户相关DTO
 * @author: niuzilian
 * @date: 2020/2/16  1:35 PM
 */
public class IchatUserDaoVerticle extends AbstractVerticle {

    private final static Logger logger = LoggerFactory.getLogger(IchatUserDaoVerticle.class);

    private EventBus eventBus;

    private JDBCClient jdbcClient;


    public interface Action {
        String SELECT_BY_PHONE = "selectByPhone";
        String INSERT = "insert";

    }


    public interface Sql {
        String SELECT_BY_PHONE = "select * from ichat_user where phone = ?";
        String INSERT = "insert into ichat_user (nickname,password,gender,phone,ichat_no) values(?,?,?,?,?)";

    }

    @Override
    public void start() throws Exception {
        super.start();
        eventBus = vertx.eventBus();
        JsonObject config = config().getJsonObject("mysql").getJsonObject("gjAssert");
        jdbcClient = JDBCClient.createShared(vertx, config, "gjAssert");
        eventBus.consumer(IchatUserDaoVerticle.class.getName(), this::consumerHandler);
    }


    private void consumerHandler(Message<JsonObject> message) {
        String action = message.headers().get("action");
        JsonObject msgBody = message.body();
        switch (action) {
            case Action.SELECT_BY_PHONE:
                String phone = msgBody.getString("phone");
                this.selectByPhone(phone, res -> {
                    if (res.succeeded()) {
                        message.reply(JsonObject.mapFrom(Response.success(res.result())));
                    } else {
                        logger.error("Failed to query user information through mobile number；phone={},e={}", phone, res.cause());
                        message.reply(JsonObject.mapFrom(Response.fail(res.cause().getMessage())));
                    }
                });
                break;
            case Action.INSERT:
                insert(msgBody, res -> {
                    if (res.succeeded()) {
                        message.reply(JsonObject.mapFrom(Response.success(res.result())));
                    } else {
                        logger.error("Failed to add user information; user={},e={}", msgBody, res.cause());
                        message.reply(JsonObject.mapFrom(Response.fail(res.cause().getMessage())));
                    }
                });
                break;
            default:
                break;
        }
    }


    private void selectByPhone(String phone, Handler<AsyncResult<JsonObject>> handler) {
        Future<SQLConnection> connectionFuture = Future.future();
        Future<ResultSet> resultSetFuture = Future.future();
        connectionFuture.setHandler(connectionRes -> {
            if (connectionRes.succeeded()) {
                SQLConnection connection = connectionRes.result();
                connection.setQueryTimeout(2000);
                JsonArray param = new JsonArray().add(phone);
                connection.queryWithParams(Sql.SELECT_BY_PHONE,param,resultSetFuture);
            } else {
                resultSetFuture.fail(connectionRes.cause());
            }
        });
        resultSetFuture.setHandler(resultSetRes -> {
            //Put connection back into the connection pool
            if(connectionFuture.isComplete()){
                connectionFuture.result().close();
            }
            if (resultSetRes.succeeded()) {
                ResultSet resultSet = resultSetRes.result();
                List<JsonObject> rows = resultSet.getRows();
                JsonObject user = (rows == null || rows.size() == 0) ? null : rows.get(0);
                handler.handle(Future.succeededFuture(user));
            } else {
                handler.handle(Future.failedFuture(resultSetRes.cause()));
            }

        });
        jdbcClient.getConnection(connectionFuture);
    }


    private void insert(JsonObject ichatUserJson, Handler<AsyncResult<Integer>> handler) {
        Future<SQLConnection> connectionFuture = Future.future();
        Future<UpdateResult> resultFuture = Future.future();
        connectionFuture.setHandler(connectionRes -> {
            if (connectionRes.succeeded()) {
                SQLConnection connection = connectionFuture.result();
                IchatUser ichatUser = ichatUserJson.mapTo(IchatUser.class);
                JsonArray param = new JsonArray();
                param.add(ichatUser.getNickname()).add(ichatUser.getPassword()).add(ichatUser.getGender())
                        .add(ichatUser.getPhone()).add(ichatUser.getIchatNo());
                connection.updateWithParams(Sql.INSERT, param, resultFuture);
            } else {
                resultFuture.fail(connectionRes.cause());
            }
        });
        resultFuture.setHandler(res -> {
            //Put connection back into the connection pool
            if(connectionFuture.isComplete()){
                connectionFuture.result().close();
            }
            if (res.succeeded()) {
                int updated = res.result().getUpdated();
                handler.handle(Future.succeededFuture(updated));
            } else {
                handler.handle(Future.failedFuture(res.cause()));
            }
        });
        jdbcClient.getConnection(connectionFuture);
    }
}
