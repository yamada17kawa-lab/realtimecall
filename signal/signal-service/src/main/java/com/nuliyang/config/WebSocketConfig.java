package com.nuliyang.config;

import com.nuliyang.interceptor.AuthHandshakeInterceptor;
import com.nuliyang.ws.SignalWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final SignalWebSocketHandler signalWebSocketHandler;
    private final AuthHandshakeInterceptor authHandshakeInterceptor;

    public WebSocketConfig(SignalWebSocketHandler signalWebSocketHandler, AuthHandshakeInterceptor authHandshakeInterceptor) {
        this.signalWebSocketHandler = signalWebSocketHandler;
        this.authHandshakeInterceptor = authHandshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(signalWebSocketHandler, "/nuliyang")
                .addInterceptors(authHandshakeInterceptor)
                .setAllowedOrigins("*");
    }
}
