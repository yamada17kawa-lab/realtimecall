package com.nuliyang.ws;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuliyang.BizException;
import com.nuliyang.ErrorCode;
import com.nuliyang.handler.SignalHandler;
import com.nuliyang.model.SignalMessage;
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



    private final ObjectMapper objectMapper;
    private final Map<String, SignalHandler> handlerMap = new HashMap<>();

    public SignalWebSocketHandler(
            ObjectMapper objectMapper,
            List<SignalHandler> handlers
    ) {
        this.objectMapper = objectMapper;

        for (SignalHandler handler : handlers) {
            handlerMap.put(handler.getType(), handler);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = session.getAttributes().get("userId").toString();
        log.info("从session中获取到userId: {}", userId);
        //存入SessionStore
        SessionStore.add(userId, session);

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        // 原始 JSON
        String payload = message.getPayload();


        // 反序列化
        SignalMessage signalMessage = objectMapper.readValue(payload, SignalMessage.class);
        log.info("收到信令消息 sessionId={}, type={}, payload={}", session.getId(), signalMessage.getType(), payload);

        // 路由
        SignalHandler handler = handlerMap.get(signalMessage.getType());
        log.info("路由到 handler={}", handler);
        if (handler == null) {
            throw new BizException(ErrorCode.UNKNOWN_SIGNAL_TYPE);
        }


        // 交给 handler
        handler.handle(signalMessage, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus status) {
        String userId = session.getAttributes().get("userId").toString();
        SessionStore.remove(userId); // 必须移除！
        log.info("清理 Session: {}", userId);
    }
}
