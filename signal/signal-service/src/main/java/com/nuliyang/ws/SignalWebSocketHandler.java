package com.nuliyang.ws;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuliyang.BizException;
import com.nuliyang.ErrorCode;
import com.nuliyang.entity.UserEntity;
import com.nuliyang.handler.SignalHandler;
import com.nuliyang.model.SignalMessage;
import com.nuliyang.store.RedisStore;
import com.nuliyang.store.SessionStore;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SignalWebSocketHandler extends TextWebSocketHandler {


    private final RedisStore redisStore;

    private final ObjectMapper objectMapper;
    private final Map<String, SignalHandler> handlerMap = new HashMap<>();



    public SignalWebSocketHandler(
            ObjectMapper objectMapper,
            List<SignalHandler> handlers,
            RedisStore redisStore) {
        this.objectMapper = objectMapper;
        this.redisStore = redisStore;
        for (SignalHandler handler : handlers) {
            handlerMap.put(handler.getType(), handler);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {


        String userId = getUserId(session);
        if (userId == null){
            throw new BizException(ErrorCode.USER_NOT_FOUND);
        }
        log.info("从session中获取到userId: {}", userId);

        Object userId2Obj = session.getAttributes().get("userId2");
        String userId2 = userId2Obj != null ? userId2Obj.toString() : null;
        log.info("从session中获取到userId2: {}", userId2);

        String roomId = getRoomId(session);
        if (roomId == null){
            throw new BizException(ErrorCode.ROOM_NOT_FOUND);
        }
        log.info("从session中获取到roomId: {}", roomId);

        String userInfoJson = objectMapper.writeValueAsString(session.getAttributes().get("userInfo"));
        log.info("从session中获取到userInfo: {}", userInfoJson);

        if (userId2 != null){
            log.info("用户{}正在与用户{}进行通话", userId, userId2);
            UserEntity userEntity1 = objectMapper.readValue(userInfoJson, UserEntity.class);
            UserEntity userEntity2 = objectMapper.readValue(redisStore.get("ws:user:" + userId2), UserEntity.class);

            userEntity1.setStatus(2);
            userEntity2.setStatus(2);

            String userEntity1Json = objectMapper.writeValueAsString(userEntity1);
            String userEntity2Json = objectMapper.writeValueAsString(userEntity2);

            redisStore.add("ws:user:" + userId, userEntity1Json);
            redisStore.add("ws:user:" + userId2, userEntity2Json);
            HashMap<String, Object> message1 = new HashMap<>();

            //给自己发送对方的id
            message1.put("type", "room");
            message1.put("data", userId2);
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message1)));

            //给自己发offer类型信息，自己发offer
            HashMap<String, Object> offerSender = new HashMap<>();
            offerSender.put("type", "offer");
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(offerSender)));

            //给对方发送自己的id
            HashMap<String, Object> message2 = new HashMap<>();
            message2.put("type", "room");
            message2.put("data", userId);
            //获取对方session并发送
            SessionStore.get(userId2).sendMessage(new TextMessage(objectMapper.writeValueAsString(message2)));

        }else {
            log.info("用户{}正在等待其他用户加入", userId);
            redisStore.createRoom("ws:user:" + userId, userInfoJson);
        }

        //存入SessionStore
        SessionStore.add(userId, session);

        //将roomId存入redis
        redisStore.createRoom("ws:room:" + userId, roomId);
        log.info("创建房间，将用户信息存入redis: {}", redisStore.get("ws:user:" + userId));



    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        // 原始 JSON
        String payload = message.getPayload();



        // 处理 ping心跳包
        if ("ping".equalsIgnoreCase(payload)) {
            log.info("收到心跳包");
            session.sendMessage(new TextMessage("pong"));

            //重置过期时间，这种性能对于redis毛毛雨！
            redisStore.expire("ws:user:" + getUserId(session));
            return;
        }

        // 反序列化
        SignalMessage signalMessage = objectMapper.readValue(payload, SignalMessage.class);
        log.info("收到信令消息 userId={}, type={}, payload={}", getUserId(session), signalMessage.getType(), payload);


        // 路由
        try {
            SignalHandler handler = handlerMap.get(signalMessage.getType());
            if (handler == null) {
                throw new BizException(ErrorCode.UNKNOWN_SIGNAL_TYPE);
            }
            log.info("路由到 handler={}", handler);
            // 交给 handler
            handler.handle(signalMessage, session);
        } catch (Exception e) {
            throw new BizException(ErrorCode.UNKNOWN_SIGNAL_TYPE);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus status) {
        String userId = getUserId(session);
        // 清理 Session
        SessionStore.remove(userId);
        // 清理 Redis
        redisStore.delete("ws:user:" + userId);
        redisStore.delete("ws:room:" + userId);
        log.info("清理 Session和Redis: {}", userId);
    }




    private String getUserId(WebSocketSession session) {
        Object userId = session.getAttributes().get("userId");
        return userId != null ? userId.toString() : "null";
    }

    private String getRoomId(WebSocketSession session) {
        Object roomId = session.getAttributes().get("roomId");
        return roomId != null ? roomId.toString() : "null";
    }

}

