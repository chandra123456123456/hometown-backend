package com.hometown.product.image;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("hometown.image")
public class ImageProperties {

    private String dir = "E:/HomeTown/.run/images";
    private String keyBase64 = "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";
    private String watermarkText = "HomeTown";
    private long urlTtlSeconds = 3600;

    public String getDir() { return dir; }
    public void setDir(String dir) { this.dir = dir; }

    public String getKeyBase64() { return keyBase64; }
    public void setKeyBase64(String keyBase64) { this.keyBase64 = keyBase64; }

    public String getWatermarkText() { return watermarkText; }
    public void setWatermarkText(String watermarkText) { this.watermarkText = watermarkText; }

    public long getUrlTtlSeconds() { return urlTtlSeconds; }
    public void setUrlTtlSeconds(long urlTtlSeconds) { this.urlTtlSeconds = urlTtlSeconds; }
}
