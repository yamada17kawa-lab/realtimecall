package com.nuliyang.filter;


import com.nuliyang.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Date;


@Component
@Slf4j
public class CustomGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        log.info("请求路径: {}", path);

        // 1️⃣ 放行登录、注册接口
        if (path.startsWith("/auth/login") || path.startsWith("/auth/register")) {
            log.info("放行登录、注册接口: {}", path);
            return chain.filter(exchange);
        }

        if (path.contains("/admin_nuliyang/upload")) {
            log.info("放行管理员上传头像接口: {}", path);
            return chain.filter(exchange);
        }

        // 2️⃣ 判断是否 WebSocket 请求
        boolean isWebSocket =
                "websocket".equalsIgnoreCase(
                        request.getHeaders().getUpgrade());

        // 3️⃣ 统一获取 token
        String token;

        if (isWebSocket) {
            // WebSocket：从 query 中取
            token = request.getQueryParams().getFirst("token");
            log.info("WebSocket 请求，token 来自 query");
        } else {
            // HTTP：从 Header 中取
            token = request.getHeaders().getFirst("Authorization");
        }

        if (token == null || token.isEmpty()) {
            log.error("未提供 token");
            return unauthorized(exchange.getResponse(), "TOKEN_INVALID");
        }

        try {
            // 4️⃣ 只解析一次 Token
            Claims claims = JwtUtil.parseToken(token);

            Long userId = Long.valueOf(claims.getSubject());
            Date expiration = claims.getExpiration();

            log.info("拦截到 token，userId={}", userId);

            if (expiration.before(new Date())) {
                log.warn("Token 已过期");
                return unauthorized(exchange.getResponse(), "TOKEN_EXPIRED");
            }

            // 5️⃣ 把用户信息传给下游服务
            ServerHttpRequest newRequest = request.mutate()
                    .header("userId", String.valueOf(userId))
                    .build();

            return chain.filter(
                    exchange.mutate()
                            .request(newRequest)
                            .build()
            );

        } catch (Exception e) {
            log.error("Token 解析失败", e);
            return unauthorized(exchange.getResponse(), "TOKEN_INVALID");
        }

    }

    @Override
    public int getOrder() {
        return -100; // 数字越小，优先级越高
    }

    private Mono<Void> unauthorized(ServerHttpResponse response,
                                    String msg) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders()
                .setContentType(MediaType.APPLICATION_JSON);

        String body = "{\"code\":401,\"msg\":\"" + msg + "\"}";

        log.info("返回 401 响应: {}", body);
        DataBuffer buffer = response.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }
}
