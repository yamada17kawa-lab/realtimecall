package com.nuliyang.config;

import com.nuliyang.store.RedisStore;
import com.nuliyang.store.SessionStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Slf4j
@Component
public class RedisKeyExpiredListener extends KeyExpirationEventMessageListener {


    private final RedisStore redisStore;


    public RedisKeyExpiredListener(RedisMessageListenerContainer listenerContainer,
                                   RedisStore redisStore) {
        super(listenerContainer);
        this.redisStore = redisStore;
        this.init();
    }



    @Override
    public void onMessage(Message message, @Nullable byte[] pattern) {
        String key = new String(message.getBody());
        log.info("key={} 过期了", key);


        if (key.startsWith("ws:user:")){
            String userId = key.replace("ws:user:", "");
            WebSocketSession session = SessionStore.get(userId);
            if (session != null){
                try {
                    //关闭session
                    session.close();
                    //移除 Session
                    SessionStore.remove(userId);

                    //删除 Redis 中的会话信息
                    redisStore.delete("ws:user:" + userId);
                    redisStore.delete("ws:room:" + userId);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }


        }

    }
}
