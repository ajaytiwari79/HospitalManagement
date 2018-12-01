package com.kairos.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CreatedBy vipulpandey on 1/12/18
 **/

public class AddRequestHeaderGatewayFilterFactory implements GatewayFilter{
    Map<StringBuffer,List<StringBuffer> >userDetails= new ConcurrentHashMap<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        if (!exchange.getRequest().getPath().toString().contains("login")){
            if (userDetails.containsKey(exchange.getRequest().getHeaders().get("Authorization"))){
                System.out.println(exchange.getRequest().getHeaders());
            }else {
                System.out.println("Its not in list ");
            }
        }else {
            System.out.println("I am login ");
        }
        return chain.filter(exchange);
       }



}
