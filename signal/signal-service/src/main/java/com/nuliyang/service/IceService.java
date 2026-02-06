package com.nuliyang.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuliyang.model.SignalMessage;
import com.nuliyang.store.SessionStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class IceService {


    private final ObjectMapper objectMapper;

    public void handleSdp(SignalMessage message, WebSocketSession session) throws IOException {
//        //获取对方id
//        String to = message.getTo();
//        //编辑massage
//        message.setFrom(session.getAttributes().get("userId").toString());
//        //获取对方session
//        WebSocketSession toSession = SessionStore.get(to);
//        if (toSession == null || !toSession.isOpen()){
//            log.info("对方已离线");
//            return;
//        }
//        log.info("获取到接收方的session,{} --> {}", to, toSession);
//        log.info("获取到发送方的session,{} --> {}", session.getAttributes().get("userId"), session);
//        log.info("当前在线人数：{}", SessionStore.getOnlineCount());
//        synchronized (toSession) {
//            try {
//                String jsonMessage = objectMapper.writeValueAsString(message);
//                toSession.sendMessage(new TextMessage(jsonMessage));
//                log.info("发送成功: {}", jsonMessage);
//            } catch (Exception e) {
//                log.error("发送失败，目标用户可能已断开: {}", e.getMessage());
//                // 如果发送失败，主动关闭这个坏掉的 session
//                try { toSession.close(); } catch (IOException ignored) {}
//            }
//        }

        String to = message.getTo();

        //获取对方session发信息
        WebSocketSession toSession = SessionStore.get(to);
        if (toSession == null || !toSession.isOpen()){
            log.info("对方已离线");
            return;
        }
        toSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        log.info("给对方发送ice信息");
    }
}
