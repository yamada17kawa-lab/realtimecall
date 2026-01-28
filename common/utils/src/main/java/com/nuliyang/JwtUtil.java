package com.nuliyang;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // 使用固定的密钥字符串，避免每次重启都变化
    private static final String SECRET_STRING = "your-constant-secret-key-must-be-at-least-32-characters-long";
    private static final SecretKey SECRET = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

    // 生成 token
    public static String generateToken(Long userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))  // 使用 userId 作为 Subject
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3L * 24 * 60 * 60 * 1000)) // 3天
                .signWith(SECRET, SignatureAlgorithm.HS256)
                .compact();
    }

    // 解析 token
    public static Claims parseToken(String token) {
        // 确保 token 格式正确，移除 Bearer 前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // 去掉 "Bearer "
        }

        return Jwts.parserBuilder()
                .setSigningKey(SECRET)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
