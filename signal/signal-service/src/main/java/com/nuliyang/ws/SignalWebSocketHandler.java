package com.nuliyang.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuliyang.BizException;
import com.nuliyang.ErrorCode;
import com.nuliyang.handler.SignalHandler;
import com.nuliyang.model.SignalMessage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

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
        log.info("WebSocket 连接建立 sessionId={}", session.getId());
        log.info("userId: {}", session.getAttributes().get("userId"));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws JsonProcessingException {
        // 1️⃣ 原始 JSON
        String payload = message.getPayload();
        log.info("收到信令消息 sessionId={}, payload={}", session.getId(), payload);


        // 2️⃣ 反序列化
        SignalMessage signal = objectMapper.readValue(payload, SignalMessage.class);

        // 3️⃣ 路由
        SignalHandler handler = handlerMap.get(signal.getType());
        if (handler == null) {
            throw new BizException(ErrorCode.UNKNOWN_SIGNAL_TYPE);
        }

        // 4️⃣ 交给 handler
        handler.handle(signal, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus status) {
        log.info("WebSocket 连接关闭 sessionId={}, status={}", session.getId(), status);
    }
}
