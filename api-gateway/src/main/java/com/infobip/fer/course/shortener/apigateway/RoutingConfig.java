package com.infobip.fer.course.shortener.apigateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutingConfig {
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/v1/urls/**")
                        .uri("http://shortener-service:8080")
                ).route(p -> p
                        .path("/v1/stats/**")
                        .uri("http://recording-service:8080")
                ).route(p -> p
                        .alwaysTrue()
                        .uri("http://redirect-service:8080")
                ).build();
    }
}