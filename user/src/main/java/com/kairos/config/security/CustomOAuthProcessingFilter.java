package com.kairos.config.security;

import com.kairos.service.auth.RedisService;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomOAuthProcessingFilter extends OAuth2AuthenticationProcessingFilter {

    private static Logger LOGGER = LoggerFactory.getLogger(CustomOAuthProcessingFilter.class);

    private TokenExtractor tokenExtractor = new BearerTokenExtractor();
    private TokenStore tokenStore;
    private RedisService redisService;
    private AuthenticationManager authenticationManager;


    /*public CustomOAuthProcessingFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }
*/
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest request=(HttpServletRequest)req;
        final HttpServletResponse response=(HttpServletResponse)res;

  /*  @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
    */ try {
            Authentication authentication = tokenExtractor.extract(request);
            if (authentication == null) {
                if (isAuthenticated()) {
                    SecurityContextHolder.clearContext();
                    LOGGER.info("Clearing Security context holder Authentication is null");
                }

            } else {
                Authentication authResult = authentication(authentication, request);
                SecurityContextHolder.getContext().setAuthentication(authResult);
            }
        } catch (OAuth2Exception failed) {
            throw new InvalidRequestException("invalid request");
        }
        chain.doFilter(request, response);
    }


    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        return true;
    }


    private Authentication authentication(Authentication authentication, HttpServletRequest request) {
        if (authentication == null) {
            throw new InvalidTokenException("Invalid token (token not found)");
        }
        String token = (String) authentication.getPrincipal();
        OAuth2Authentication auth = loadAuthentication(token, request);
        if (auth == null) {
            throw new InvalidTokenException("unable to access authentication");
        } else {
            if (!redisService.checkIfUserExistInRedis(auth.getName())) {
                throw new InvalidTokenException("Login again password updated or token expired");
            }
        }
        auth.setDetails(authentication.getDetails());
        auth.setAuthenticated(true);
        return auth;

    }


    private OAuth2Authentication loadAuthentication(String accessToken, HttpServletRequest request) {
        OAuth2Authentication authentication;
        OAuth2AccessToken token = tokenStore.readAccessToken(accessToken);
        if (token == null) {
            throw new InvalidTokenException("invalid token");
        } else if (token.isExpired()) {
            authentication = tokenStore.readAuthentication(accessToken);
            removeTokenFromRedis(authentication.getUserAuthentication().getName(), request.getRemoteAddr());
            tokenStore.readAccessToken(accessToken);
            throw new InvalidTokenException("Token texpired");
        }
        authentication = tokenStore.readAuthentication(accessToken);
        if (authentication == null) {
            throw new InvalidTokenException("Invalid access token: " + token);
        }
        return authentication;
    }


    @Override
    public void afterPropertiesSet() {

    }

    private void removeTokenFromRedis(String userName, String clientIp) {
        redisService.removeUserTokenFromRedisByClientIpAddress(userName, clientIp);
    }

    public void setTokenStore(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    public void setRedisService(RedisService redisService) {
        this.redisService = redisService;
    }


}
