package com.hometown.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * Issues and validates HS256 JWTs. Shared by user-service (issuer) and the
 * gateway / other services (validators). Stateless — safe as a singleton bean.
 */
public class JwtService {

    private final JwtProperties props;
    private final SecretKey key;

    public JwtService(JwtProperties props) {
        this.props = props;
        this.key = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /** Issue an access token carrying the user id (subject), email and role. */
    public String generateAccessToken(Long userId, String email, String role) {
        return build(String.valueOf(userId),
                Map.of("email", email, "role", role, "type", "access"),
                props.getAccessTokenTtlMillis());
    }

    /** Issue a refresh token (subject = user id). */
    public String generateRefreshToken(Long userId) {
        return build(String.valueOf(userId),
                Map.of("type", "refresh"),
                props.getRefreshTokenTtlMillis());
    }

    private String build(String subject, Map<String, ?> claims, long ttlMillis) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .issuer(props.getIssuer())
                .subject(subject)
                .claims(claims)
                .issuedAt(new Date(now))
                .expiration(new Date(now + ttlMillis))
                .signWith(key)
                .compact();
    }

    /** Parse and verify a token, returning its claims. Throws if invalid/expired. */
    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /** True if the token is well-formed, correctly signed and unexpired. */
    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long extractUserId(String token) {
        return Long.valueOf(parse(token).getSubject());
    }

    public String extractRole(String token) {
        return parse(token).get("role", String.class);
    }

    public String extractEmail(String token) {
        return parse(token).get("email", String.class);
    }
}
