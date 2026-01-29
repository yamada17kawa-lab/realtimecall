package com.nuliyang.handler.handlerimpl;

import com.nuliyang.handler.SignalHandler;
import com.nuliyang.model.SignalMessage;
import com.nuliyang.service.SdpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;


@RequiredArgsConstructor
@Component
@Slf4j
public class SdpHandlerImpl implements SignalHandler {


    private final SdpService sdpService;

    @Override
    public String getType() {
        return "sdp";
    }

    @Override
    public void handle(SignalMessage message, WebSocketSession session) throws IOException {
        log.info("进入sdphandler路由");
        sdpService.handleSdp(message, session);
    }
}
