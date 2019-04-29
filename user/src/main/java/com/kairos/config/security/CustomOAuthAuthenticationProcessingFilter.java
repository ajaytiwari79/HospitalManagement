package com.kairos.config.security;

import com.kairos.service.auth.RedisService;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomOAuthAuthenticationProcessingFilter extends OAuth2AuthenticationProcessingFilter {

    private static Logger LOGGER = LoggerFactory.getLogger(CustomOAuthAuthenticationProcessingFilter.class);

    private TokenExtractor tokenExtractor = new BearerTokenExtractor();
    private TokenStore tokenStore;
    private RedisService redisService;
    private ExceptionService exceptionService;

    public CustomOAuthAuthenticationProcessingFilter(TokenStore tokenStore, RedisService redisService, ExceptionService exceptionService) {
        this.tokenStore = tokenStore;
        this.redisService = redisService;
        this.exceptionService = exceptionService;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;
        try {
            Authentication authentication = tokenExtractor.extract(request);
            if (authentication == null) {
                if (isAuthenticated()) {
                    SecurityContextHolder.clearContext();
                    LOGGER.info("Clearing Security context holder Authentication is null");
                }
                exceptionService.internalServerError("message.authentication.null");

            } else {
                Authentication authResult = authentication(authentication, request);
                SecurityContextHolder.getContext().setAuthentication(authResult);
            }
        } catch (OAuth2Exception failed) {
            exceptionService.internalServerError("invalid request");
        }
        chain.doFilter(request, response);
    }


    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !(authentication == null || authentication instanceof AnonymousAuthenticationToken);

    }


    private Authentication authentication(Authentication authentication, HttpServletRequest request) {

        String token = (String) authentication.getPrincipal();
        OAuth2Authentication auth = loadAuthentication(token, request);
        if (auth == null) {
            exceptionService.invalidTokenException("message.authentication.loadAuthentication.null");
        } else {
            if (!redisService.checkIfUserExistInRedis(auth.getName())) {
                exceptionService.userNotFoundInRedis("message.user.notFoundInRedis");
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
            exceptionService.invalidTokenException("message.authentication.loadAuthentication.null");
        } else if (token.isExpired()) {
            authentication = tokenStore.readAuthentication(accessToken);
            removeTokenFromRedis(authentication.getUserAuthentication().getName(), request.getRemoteAddr());
            tokenStore.readAccessToken(accessToken);
           exceptionService.invalidTokenException("message.token.expire",token);
        }
        authentication = tokenStore.readAuthentication(accessToken);
        if (authentication == null) {
           exceptionService.invalidTokenException("message.token.expired",token);
        }
        return authentication;
    }


    @Override
    public void afterPropertiesSet() {

        // no need to define
    }

    private void removeTokenFromRedis(String userName, String clientIp) {
        redisService.removeUserTokenFromRedisByClientIpAddress(userName, clientIp);
    }

}
