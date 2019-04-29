package com.kairos.config.security;

import com.kairos.service.auth.RedisService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.inject.Inject;

@EnableResourceServer
@Configuration
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {


    @Inject
    private RedisService redisService;
    @Inject
    private JwtAccessTokenConverter jwtAccessTokenConverter;
    @Inject
    private ExceptionService exceptionService;


    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        super.configure(resources);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/resources/**", "/configuration/ui", "/swagger-resources/**/**", "/swagger-ui.html", "/v2/api-docs", "/webjars/**").permitAll()
                .antMatchers("/actuator/**", "/api/v1/legal").permitAll()
                .antMatchers("/**").authenticated()
                .and().addFilterBefore(getAuthenticationFilter(), AbstractPreAuthenticatedProcessingFilter.class);
    }

    public OAuth2AuthenticationProcessingFilter getAuthenticationFilter() {
        return new CustomOAuthAuthenticationProcessingFilter(new JwtTokenStore(jwtAccessTokenConverter), redisService, exceptionService);
    }

}
