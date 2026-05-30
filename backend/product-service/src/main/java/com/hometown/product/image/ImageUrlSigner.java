package com.hometown.product.image;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class ImageUrlSigner {

    private static final String PREFIX = "/api/images/";

    private final byte[] key;
    private final long ttlSeconds;

    public ImageUrlSigner(ImageProperties props) {
        this.key = Base64.getDecoder().decode(props.getKeyBase64());
        this.ttlSeconds = props.getUrlTtlSeconds();
    }

    // Appends ?exp=&sig= to our own image urls; leaves external urls untouched.
    public String sign(String url) {
        if (url == null || !url.startsWith(PREFIX)) return url;
        String base = stripQuery(url);
        String code = base.substring(PREFIX.length());
        long exp = nowSeconds() + ttlSeconds;
        return base + "?exp=" + exp + "&sig=" + hmac(code + "|" + exp);
    }

    public boolean verify(String code, Long exp, String sig) {
        if (exp == null || sig == null) return false;
        if (exp < nowSeconds()) return false;
        return constantTimeEquals(hmac(code + "|" + exp), sig);
    }

    public String stripQuery(String url) {
        if (url == null) return null;
        int q = url.indexOf('?');
        return q >= 0 ? url.substring(0, q) : url;
    }

    private long nowSeconds() {
        return System.currentTimeMillis() / 1000L;
    }

    private String hmac(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(raw);
        } catch (Exception e) {
            throw new IllegalStateException("HMAC failure", e);
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) return false;
        int r = 0;
        for (int i = 0; i < a.length(); i++) r |= a.charAt(i) ^ b.charAt(i);
        return r == 0;
    }
}
