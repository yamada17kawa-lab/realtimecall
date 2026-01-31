package com.nuliyang.filter;


import com.nuliyang.JwtUtil;
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

        // 1️⃣ 放行登录、注册接口
        if (path.startsWith("/auth/login") || path.startsWith("/auth/register"))
        {
            log.info("放行登录、注册接口: {}",  path);
            return chain.filter(exchange);
        }

        if (path.contains("/admin_nuliyang/upload")){
            log.info("放行管理员上传头像接口: {}",  path);
            return chain.filter(exchange);
        }

        // 2️⃣ 获取 Authorization
        String authorization = request.getHeaders().getFirst("Authorization");

        if (authorization == null || authorization.isEmpty()) {
            log.error("未提供 Authorization 头");
            return unauthorized(exchange.getResponse(), "TOKEN_INVALID");
        }

        try {
            // 3️⃣ 校验 Token（你自己的 JwtUtil）
            log.info("拦截到token中的subject: {}", JwtUtil.parseToken(authorization).getSubject());
            Long userId = Long.valueOf(JwtUtil.parseToken(authorization).getSubject());

            Date expiration = JwtUtil.parseToken(authorization).getExpiration();
            if (expiration.before(new Date())) {
                log.warn("Token 已过期");
                return unauthorized(exchange.getResponse(), "TOKEN_EXPIRED");
            }

            // 4️⃣ 把用户信息传给下游服务
            ServerHttpRequest newRequest = request.mutate()
                    .header("userId", String.valueOf(userId))
                    .build();

            return chain.filter(exchange.mutate()
                    .request(newRequest)
                    .build());

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
