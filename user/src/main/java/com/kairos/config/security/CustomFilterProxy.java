package com.kairos.config.security;

import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

public class CustomFilterProxy extends FilterChainProxy {


   /* public CustomFilterProxy(SecurityFilterChain chain) {
        chain.getFilters().forEach(filter -> filter.getClass().equals(OAuth2AuthenticationProcessingFilter.class));
        super.
    }*/
}
