package com.kairos.config.security;


import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@Order(1)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {


    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .antMatchers("/configuration/ui", "/swagger-resources/**/**", "/swagger-ui.html", "/v2/api-docs").authenticated()
                .antMatchers("/resources/**", "/webjars/**","/static/**","/css/**","/js/**","/images/**").permitAll()
                .antMatchers("/actuator/**", "/api/v1/legal").permitAll()
                .antMatchers("/public/legal/**/**").permitAll();
    }



}
