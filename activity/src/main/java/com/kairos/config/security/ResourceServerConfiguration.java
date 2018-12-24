package com.kairos.config.security;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import static com.kairos.constants.ApiConstants.*;
import static com.kairos.constants.AppConstants.API_CREATE_KMD_TASK_DEMAND;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter{
	
    Logger log = LoggerFactory.getLogger(ResourceServerConfiguration.class);

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/webjars/**","/resources/**","/swagger-resources/**/**","/swagger-ui.html","/v2/api-docs").permitAll()
                .antMatchers( API_ORGANIZATION_URL + "/time_care/**").permitAll()
                .antMatchers( API_ORGANIZATION_UNIT_URL + COUNTRY_URL + "/time_care/**").permitAll()
                .antMatchers(API_CREATE_KMD_TASK_DEMAND).permitAll()
                .antMatchers(API_ORGANIZATION_UNIT_URL+"/api/v1/ws/**").permitAll()
                .antMatchers("/planner/vrp_completed/**").permitAll()
                .antMatchers(API_ORGANIZATION_UNIT_URL+"/getShiftPlanningInfo").permitAll()
                .antMatchers(API_ORGANIZATION_UNIT_URL+"/sub-shifts").permitAll()
                //.antMatchers(API_V1+ SCHEDULER_EXECUTE_JOB).permitAll()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/**").authenticated();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer config) {
        config.tokenServices(tokenServices());
    }
 
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }
 
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
    	 JwtAccessTokenConverter converter = new CustomJwtAccessTokenConverter();
    	    /*Resource resource = new ClassPathResource("public.txt");
    	    String publicKey = null;
    	    try {
    	        publicKey = IOUtils.toString(resource.getInputStream(),"UTF-8");
    	    } catch (final IOException e) {
    	        throw new RuntimeException(e);
    	    }

          converter.setVerifierKey(publicKey);*/
        //anilm2 use commented code if certificate not install
        converter.setSigningKey("123456");
          	    return converter;
    }
 
    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        return defaultTokenServices;
    }
}
