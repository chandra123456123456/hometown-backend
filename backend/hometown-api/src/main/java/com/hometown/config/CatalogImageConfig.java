package com.hometown.config;

import com.hometown.audit.AuditInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CatalogImageConfig implements WebMvcConfigurer {

    private final AuditInterceptor auditInterceptor;

    public CatalogImageConfig(AuditInterceptor auditInterceptor) {
        this.auditInterceptor = auditInterceptor;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/api/catalog/**")
                .addResourceLocations("classpath:/static/catalog/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(auditInterceptor).addPathPatterns("/api/**");
    }
}
