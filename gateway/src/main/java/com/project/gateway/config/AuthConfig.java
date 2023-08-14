package com.project.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Configuration
public class AuthConfig {
    @Value("${server.port}")
    private int port;

    @Value("${server.hostname}")
    private String host;

    @Bean
    public RouteLocator locator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(rout -> rout.path("/api/user/**",
                                "/api/group/**",
                                "/api/statement/**",
                                "/api/student/**")
                        .uri("lb://user-service")
                )
                .route(rout -> rout.path("/api/schedule/**")
                        .uri("lb://schedule-service")
                )
                .route(rout -> rout.path("/api/event/**")
                        .uri("lb://event-service")
                )
                .route(route -> route.path("/user-service/**",
                                "/schedule-service/**",
                                "/event-service/**")
                        .filters(filterSpec -> filterSpec.changeRequestUri(exchange -> {
                            var serverRequest = exchange.getRequest();
                            var path = serverRequest.getPath();
                            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(serverRequest.getURI())
                                    .host(host)
                                    .port(port)
                                    .replacePath(path.subPath(2).toString());

                            return Optional.of(uriBuilder.build().toUri());
                        }))
                        .uri("lb://ignored-uri")
                )
                .build();
    }
}
