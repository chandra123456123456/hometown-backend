package com.hometown;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.hometown")
@EntityScan("com.hometown")
@EnableJpaRepositories("com.hometown")
@ComponentScan(
        basePackages = "com.hometown",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        com.hometown.user.UserServiceApplication.class,
                        com.hometown.product.ProductServiceApplication.class,
                        com.hometown.cart.CartServiceApplication.class,
                        com.hometown.order.OrderServiceApplication.class,
                        com.hometown.payment.PaymentServiceApplication.class,
                        com.hometown.shipping.ShippingServiceApplication.class,
                        com.hometown.analytics.AnalyticsServiceApplication.class,
                        com.hometown.user.config.SecurityConfig.class,
                        com.hometown.product.config.SecurityConfig.class,
                        com.hometown.cart.config.SecurityConfig.class,
                        com.hometown.order.config.SecurityConfig.class,
                        com.hometown.payment.config.SecurityConfig.class
                }
        )
)
public class HomeTownApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomeTownApiApplication.class, args);
    }
}
