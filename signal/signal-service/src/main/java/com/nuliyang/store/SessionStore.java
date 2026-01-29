package com.nuliyang.store;


import org.springframework.web.socket.WebSocketSession;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class SessionStore {
    // Key 为用户唯一标识（如 userId），Value 为对应的 Session 对象
    private static final Map<String, WebSocketSession> SESSION_POOL = new ConcurrentHashMap<>();

    // 添加 Session
    public static void add(String userId, WebSocketSession session) {
        SESSION_POOL.put(userId, session);
    }

    // 移除 Session
    public static void remove(String userId) {
        SESSION_POOL.remove(userId);
    }

    // 获取 Session
    public static WebSocketSession get(String userId) {
        return SESSION_POOL.get(userId);
    }

    // 获取当前在线人数
    public static int getOnlineCount() {
        return SESSION_POOL.size();
    }
}
