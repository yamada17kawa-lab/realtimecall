package com.nuliyang.interceptor;

import com.nuliyang.BizException;
import com.nuliyang.ErrorCode;
import com.nuliyang.JwtUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class AuthHandshakeInterceptor implements HandshakeInterceptor {


    private boolean checkToken(String token) {

        if (token == null || token.isEmpty()) {
            return false;
        }

        try {

            log.info("拦截到token中的subject: {}", JwtUtil.parseToken(token).getSubject());
            Date expiration = JwtUtil.parseToken(token).getExpiration();
            if (expiration.before(new Date())) {
                throw new BizException(ErrorCode.TOKEN_INVALID);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request,
                                   @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler,
                                   @NonNull Map<String, Object> attributes) {
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return false;
        }

        // 1️⃣ 取 token（一般放在 Authorization）
        String auth = servletRequest.getServletRequest()
                .getHeader("Authorization");

        if (auth == null || auth.isEmpty()) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
        log.info("拦截到Authorization: {}", auth);

        // 2️⃣ 校验 token
        if (!checkToken(auth)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        String token = auth.replace("Bearer ", "");



        // 3️⃣ 解析 userId
        Long userId = Long.valueOf(JwtUtil.parseToken(token).getSubject());

        // 4️⃣ 存入 WebSocket Session
        attributes.put("userId", userId);
        log.info("将userId存入session: {}", attributes.get("userId"));

        return true; // 允许建立连接
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response, @NonNull WebSocketHandler wsHandler, @Nullable Exception exception) {
        //不做处理
    }
}

