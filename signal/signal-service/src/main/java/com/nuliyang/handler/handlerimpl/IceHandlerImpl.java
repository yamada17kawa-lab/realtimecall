package com.nuliyang.handler.handlerimpl;

import com.nuliyang.handler.SignalHandler;
import com.nuliyang.model.SignalMessage;
import com.nuliyang.service.IceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;


@RequiredArgsConstructor
@Component
@Slf4j
public class IceHandlerImpl implements SignalHandler {


    private final IceService iceService;

    @Override
    public String getType() {
        return "ice";
    }

    @Override
    public void handle(SignalMessage message, WebSocketSession session) throws IOException {
        log.info("进入icehandler路由");
        iceService.handleSdp(message, session);
    }
}
