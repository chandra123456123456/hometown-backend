package com.hometown.common;

import com.hometown.common.security.JwtProperties;
import com.hometown.common.security.JwtService;
import com.hometown.common.web.GlobalExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Auto-configures shared HomeTown beans for every service that depends on the
 * common module — no component scanning required.
 *
 * JWT beans load everywhere (servlet and reactive). The servlet-only global
 * error handler is guarded so the reactive gateway can safely reuse this module.
 */
@AutoConfiguration
@EnableConfigurationProperties(JwtProperties.class)
public class CommonAutoConfiguration {

    @Bean
    public JwtService jwtService(JwtProperties props) {
        return new JwtService(props);
    }

    /** Registered only in servlet web apps (not the reactive gateway). */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnClass(HttpServletRequest.class)
    @Import(GlobalExceptionHandler.class)
    static class ServletWebConfig {
    }
}
