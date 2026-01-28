package com.nuliyang.handler;

import com.nuliyang.model.SignalMessage;
import org.springframework.web.socket.WebSocketSession;

public interface SignalHandler {

    /**
     * 当前 handler 能处理的信令类型
     */
    String getType();

    /**
     * 处理信令
     */
    void handle(SignalMessage message, WebSocketSession session);
}
