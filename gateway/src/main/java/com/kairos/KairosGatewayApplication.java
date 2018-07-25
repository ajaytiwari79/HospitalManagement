package com.kairos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class KairosGatewayApplication {
    private static final String ALLOWED_HEADERS = "X-Requested-With,access-control-allow-origin,Authorization,authorization,Origin,Content-Type,Version";
    private static final String ALLOWED_METHODS = "GET,PUT,POST,DELETE,OPTIONS";
    private static final String ALLOWED_ORIGIN = "*";
    private static final String MAX_AGE = "3600";
    public static void main(String[] args) {
        SpringApplication.run(KairosGatewayApplication.class, args);
    }

    @Bean
    public DiscoveryClientRouteDefinitionLocator discoveryRouts(DiscoveryClient ds){
        return new DiscoveryClientRouteDefinitionLocator(ds);
    }

    @Bean
    public WebFilter corsFilter() {
        return (ServerWebExchange ctx, WebFilterChain chain) -> {
            ServerHttpRequest request = ctx.getRequest();
                ServerHttpResponse response = ctx.getResponse();
                HttpHeaders headers = response.getHeaders();
                headers.set("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
                headers.set("Access-Control-Allow-Credentials", "true");
                headers.set("Access-Control-Allow-Methods", ALLOWED_METHODS);
                headers.set("Access-Control-Max-Age", MAX_AGE);
                headers.set("Access-Control-Allow-Headers",ALLOWED_HEADERS);
                if (request.getMethod() == HttpMethod.OPTIONS) {
                    response.setStatusCode(HttpStatus.OK);
                    return Mono.empty();
                }
            return chain.filter(ctx);
        };
    }




}

