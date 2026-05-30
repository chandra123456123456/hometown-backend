package com.hometown.config;

import com.hometown.audit.AuditInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CatalogImageConfig implements WebMvcConfigurer {

    private final AuditInterceptor auditInterceptor;
    private final String shotsDir;

    public CatalogImageConfig(AuditInterceptor auditInterceptor,
                              @org.springframework.beans.factory.annotation.Value("${hometown.shots.dir:E:/HomeTown/.run/shots}") String shotsDir) {
        this.auditInterceptor = auditInterceptor;
        this.shotsDir = shotsDir;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/api/catalog/**")
                .addResourceLocations("classpath:/static/catalog/");
        // Uploaded short videos (range-streamed for seeking).
        registry.addResourceHandler("/api/shots/media/**")
                .addResourceLocations("file:" + (shotsDir.endsWith("/") ? shotsDir : shotsDir + "/"));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(auditInterceptor).addPathPatterns("/api/**");
    }
}
