package com.kairos.security;


//@Configuration
//@EnableResourceServer
//public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
//
//    Logger log = LoggerFactory.getLogger(ResourceServerConfiguration.class);
//
////    @Override
////    public void configure(HttpSecurity http) throws Exception {
////        http.csrf().disable().authorizeRequests()
////                .antMatchers("/resources/**", "/configuration/ui",  "/swagger-resources/**/**", "/swagger-ui.html", "/v2/api-docs","/webjars/**").permitAll()
////                .antMatchers("/actuator/**").permitAll()
////                .antMatchers("/**").authenticated();
////    }
//
//    @Override
//    public void configure(ResourceServerSecurityConfigurer config) {
//        config.tokenServices(tokenServices());
//    }
//
//
//    @Bean
//    public TokenStore tokenStore() {
//        return new JwtTokenStore(accessTokenConverter());
//    }
//
//
//    @Bean
//    public JwtAccessTokenConverter accessTokenConverter() {
//        JwtAccessTokenConverter converter = new CustomJwtAccessTokenConverter();
//        converter.setSigningKey("123456");
//        return converter;
//    }
//
//    @Bean
//    @Primary
//    public DefaultTokenServices tokenServices() {
//        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
//        defaultTokenServices.setTokenStore(tokenStore());
//        return defaultTokenServices;
//    }
//
//}
