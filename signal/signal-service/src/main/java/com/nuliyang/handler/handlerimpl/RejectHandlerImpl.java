package com.nuliyang.handler.handlerimpl;


import com.nuliyang.handler.SignalHandler;
import com.nuliyang.model.SignalMessage;
import com.nuliyang.store.RedisStore;
import com.nuliyang.store.SessionStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Slf4j
@Component
public class RejectHandlerImpl implements SignalHandler {


    private final RedisStore redisStore;

    public RejectHandlerImpl(RedisStore redisStore) {
        this.redisStore = redisStore;
    }

    @Override
    public String getType() {
        return "reject";
    }

    @Override
    public void handle(SignalMessage message, WebSocketSession session) throws IOException {
        String userId = getUserId(session);

        String targetUserId = message.getTo();

        if (targetUserId != null){
            log.info("存在通话好友，将好友断开");
            SessionStore.get(targetUserId).close();

            SessionStore.remove(targetUserId);

            redisStore.delete("ws:user:" + targetUserId);

            redisStore.delete("ws:room:" + targetUserId);
        }

        //获取双方session并断开连接
        SessionStore.get(userId).close();


        //清空本地缓存
        SessionStore.remove(userId);


        redisStore.delete("ws:user:" + userId);


        redisStore.delete("ws:room:" + userId);


        log.info("通话结束");




    }


    private String getUserId(WebSocketSession session) {
        Object userId = session.getAttributes().get("userId");
        return userId != null ? userId.toString() : "unknown";
    }

    private String getRoomId(WebSocketSession session) {
        Object roomId = session.getAttributes().get("roomId");
        return roomId != null ? roomId.toString() : "unknown";
    }
}
