package com.kairos.config.security;

import com.kairos.service.auth.UserService;
import com.kairos.service.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import javax.inject.Inject;

@Configuration
@EnableAuthorizationServer
public class JWTOAuth2Config extends AuthorizationServerConfigurerAdapter {

    public static final String CLIENT_CREDENTIALS = "client_credentials";
    public static final String REFRESH_TOKEN = "refresh_token";
    @Autowired
    private AuthenticationManager authenticationManager;
    @Inject
    private RedisService redisService;
    @Autowired
    private UserService userService;

    private final int REFRESH_TOKEN_VALIDITY_SECONDS = 60 * 60 * 24 * 30; // default 30 days.

    private final int ACCESS_TOKEN_VALIDITY_SECONDS = 60 * 60 * 24 * 30; // default 12 hours. But we changed it to 30 days(for testing)

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.authenticationManager(authenticationManager).accessTokenConverter(jwtAccessTokenConverter())
                .tokenServices(customTokenServices());
    }

    @Bean
    @Primary
    public AuthorizationServerTokenServices customTokenServices() {
        final JwtTokenStore jwtTokenStore = new JwtTokenStore(this.jwtAccessTokenConverter());
        DefaultTokenServices defaultTokenServices = new CustomDefaultTokenServices(userService, redisService);
        defaultTokenServices.setTokenStore(jwtTokenStore);
        defaultTokenServices.setTokenEnhancer(this.jwtAccessTokenConverter());
        defaultTokenServices.setRefreshTokenValiditySeconds(REFRESH_TOKEN_VALIDITY_SECONDS);
        defaultTokenServices.setAccessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS);
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

        clients.inMemory()
                .withClient("kairos")
                .secret("$2a$10$CA3JnlNGZpRIqvBe904d1eOBjoJXX0rLatl7kMQL9mGPyGVm6xYj.")
                .authorizedGrantTypes(REFRESH_TOKEN, "password", CLIENT_CREDENTIALS)
                .scopes("webclient", "mobileclient")
                .and()
                .withClient("activity-service")
                .secret("task")
                .authorizedGrantTypes(CLIENT_CREDENTIALS, REFRESH_TOKEN)
                .scopes("server")
                .and()
                .withClient("user-service")
                .secret("user")
                .authorizedGrantTypes(CLIENT_CREDENTIALS, REFRESH_TOKEN)
                .scopes("server");


    }


    @Bean
    @Primary
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new CustomJwtAccessTokenConverter();
        //anilm2 use commented code if certificate not install
        converter.setSigningKey("123456");
        /*try{
            Resource resource=new FileSystemResource("/home/anil/springcert.jks");
            final KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource, "kairos".toCharArray());
            converter.setKeyPair(keyStoreKeyFactory.getKeyPair("kairos"));

        }catch (Exception e){


        }*/
        return converter;
    }

    @Override
    public void configure(final AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
        oauthServer.passwordEncoder(passwordEncoder());
    }






    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}