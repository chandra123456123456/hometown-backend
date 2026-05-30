package com.hometown.common.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT configuration, bound from {@code hometown.jwt.*}.
 * Secret is supplied via env var (JWT_SECRET) in every environment.
 */
@ConfigurationProperties(prefix = "hometown.jwt")
public class JwtProperties {

    /** HMAC signing secret (min 32 chars for HS256). Injected via JWT_SECRET. */
    private String secret = "change-me-in-env-this-is-a-dev-only-fallback-secret-32+";

    /** Access-token lifetime in milliseconds (default 1 hour). */
    private long accessTokenTtlMillis = 3_600_000L;

    /** Refresh-token lifetime in milliseconds (default 7 days). */
    private long refreshTokenTtlMillis = 604_800_000L;

    /** Token issuer claim. */
    private String issuer = "hometown";

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getAccessTokenTtlMillis() {
        return accessTokenTtlMillis;
    }

    public void setAccessTokenTtlMillis(long accessTokenTtlMillis) {
        this.accessTokenTtlMillis = accessTokenTtlMillis;
    }

    public long getRefreshTokenTtlMillis() {
        return refreshTokenTtlMillis;
    }

    public void setRefreshTokenTtlMillis(long refreshTokenTtlMillis) {
        this.refreshTokenTtlMillis = refreshTokenTtlMillis;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
}
