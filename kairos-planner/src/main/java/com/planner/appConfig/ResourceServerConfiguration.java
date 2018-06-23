package com.planner.appConfig;




//@Configuration
//@EnableResourceServer
public class ResourceServerConfiguration {//extends ResourceServerConfigurerAdapter
	
    /*Logger log = LoggerFactory.getLogger(ResourceServerConfiguration.class);

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
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
    	    *//*Resource resource = new ClassPathResource("public.txt");
    	    String publicKey = null;
    	    try {
    	        publicKey = IOUtils.toString(resource.getInputStream(),"UTF-8");
    	    } catch (final IOException e) {
    	        throw new RuntimeException(e);
    	    }

          converter.setVerifierKey(publicKey);*//*
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
    }*/
}
