package com.nuliyang.interceptor;

import com.nuliyang.BizException;
import com.nuliyang.ErrorCode;
import com.nuliyang.JwtUtil;
import com.nuliyang.store.UserStore;
import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
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



    private final UserStore userStore;

    public AuthHandshakeInterceptor(UserStore userStore) {
        this.userStore = userStore;
    }

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
        log.info("开始websocket拦截");

        //检查请求对象是否为ServletServerHttpRequest类型
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            log.error("请求对象不是ServletServerHttpRequest类型");
            return false;
        }


        //解析URL
        String query = request.getURI().getQuery();
        if (query == null || query.isEmpty()) {
            log.info("URL查询参数为空，跳过解析");
            return false; // 或者根据业务需求返回其他值
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("="); // 防止无值参数（如 ?debug）
            if (idx > 0 && idx < pair.length() - 1) {
                String key = pair.substring(0, idx);
                String value = pair.substring(idx + 1);
                if ("roomId".equals(key)) {
                    attributes.put("roomId", value);
                    log.info("从URL参数中提取 roomId: {}", value);
                }
                if ("token".equals(key)) {
                    if (!checkToken(value)) {
                        log.error("token无效");
                        return false;
                    }
                    Claims claims = JwtUtil.parseToken(value);
                    Long userId = Long.valueOf(claims.getSubject());
                    attributes.put("userId", userId);
                    log.info("从URL参数中提取 userId: {}", value);
                }
                if ("userId2".equals(key)) {
                    attributes.put("userId2", value);
                    log.info("从URL参数中提取 userId2: {}", value);
                }
            }
        }
//
//        // 3️⃣ 解析 userId
//        Long userId = Long.valueOf(JwtUtil.parseToken(token).getSubject());

//        // 4️⃣ 存入 WebSocket Session
//        attributes.put("userId", userId);
//        log.info("将userId存入session: {}", attributes.get("userId"));

        //查出用户信息，存入session
        //将用户改为在线
        Long userId = (Long) attributes.get("userId");
        attributes.put("userInfo", userStore.getUserByUserId(userId).setStatus(1).setRoomId((String) attributes.get("roomId")));
        log.info("将用户信息存入session: {}", attributes.get("userInfo"));

        return true; // 允许建立连接
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response, @NonNull WebSocketHandler wsHandler, @Nullable Exception exception) {
        //不做处理
    }
}

