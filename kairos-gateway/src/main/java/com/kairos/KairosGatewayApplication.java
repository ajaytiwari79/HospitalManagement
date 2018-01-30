package com.kairos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@SpringBootApplication
@EnableWebFlux
public class KairosGatewayApplication implements WebFluxConfigurer{

    public static void main(String[] args) {
        SpringApplication.run(KairosGatewayApplication.class, args);
    }

    @Bean
    public DiscoveryClientRouteDefinitionLocator discoveryRouts(DiscoveryClient ds){
        return new DiscoveryClientRouteDefinitionLocator(ds);
    }
  @Override
   public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedHeaders("*")
                .allowedMethods("OPTIONS","HEAD","GET","PUT","POST","DELETE","PATCH")
                .allowCredentials(true)
                .allowedOrigins("*");
    }


}

