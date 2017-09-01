package com.kairos.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class JWTOAuth2Config extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager).accessTokenConverter(jwtAccessTokenConverter())
                .tokenServices(customTokenServices());
    }

    @Bean
    @Primary
    public AuthorizationServerTokenServices customTokenServices() {
        DefaultTokenServices defaultTokenServices =new  CustomDefaultTokenServices();
        final JwtTokenStore jwtTokenStore = new JwtTokenStore(this.jwtAccessTokenConverter());
        defaultTokenServices.setTokenStore(jwtTokenStore);
        defaultTokenServices.setTokenEnhancer(this.jwtAccessTokenConverter());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

        clients.inMemory()
                .withClient("kairos")
                .secret("kairos")
                .authorizedGrantTypes("refresh_token","password","client_credentials")
                .scopes("webclient", "mobileclient")
     			.and()
     					.withClient("activity-service")
     					.secret("task")
     					.authorizedGrantTypes("client_credentials", "refresh_token")
     					.scopes("server")
     			.and()
     					.withClient("user-service")
     					.secret("user")
     					.authorizedGrantTypes("client_credentials", "refresh_token")
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
    }
}