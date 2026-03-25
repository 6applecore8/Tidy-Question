package com.tidyquestion.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类，封装 token 生成与解析。
 *
 * 设计说明：
 * - 不做兜底密钥，配置错误时直接抛出明确异常，避免线上误用弱密钥。
 * - 通过长度校验确保 HS256 至少 32 字节密钥。
 */
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final int expireHours;

    public JwtTokenProvider(@Value("${app.jwt.signing-key:}") String signingKey,
                            @Value("${app.jwt.expire-hours:24}") int expireHours) {
        String key = signingKey == null ? "" : signingKey.trim();
        int keyLength = key.getBytes(StandardCharsets.UTF_8).length;

        if (keyLength < 32) {
            throw new IllegalStateException(
                    "Invalid JWT signing key: app.jwt.signing-key must be at least 32 bytes. " +
                    "Please set it in application-local.yml or APP_JWT_SIGNING_KEY. Current length=" + keyLength
            );
        }
        if (expireHours <= 0) {
            throw new IllegalStateException("Invalid JWT expire hours: app.jwt.expire-hours must be > 0");
        }

        this.secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
        this.expireHours = expireHours;
    }

    /**
     * 生成访问令牌。
     */
    public String generateToken(Long userId, String username) {
        Instant now = Instant.now();
        Instant exp = now.plus(expireHours, ChronoUnit.HOURS);
        return Jwts.builder()
                .subject(username)
                .claims(Map.of("userId", userId))
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(secretKey)
                .compact();
    }

    /**
     * 校验并解析 JWT。
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
