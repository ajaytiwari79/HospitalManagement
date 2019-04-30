package com.kairos.config.security;

import com.kairos.service.exception.ExceptionService;
import com.kairos.service.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

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
    private AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
    private AuthenticationEventPublisher eventPublisher = new NullEventPublisher();
    private final boolean debug=true;



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
                throw new InvalidTokenException(exceptionService.convertMessage("message.authentication.null"));

            } else {
                Authentication authResult = authentication(authentication, request);
                SecurityContextHolder.getContext().setAuthentication(authResult);
            }
        } catch (OAuth2Exception failed) {
            SecurityContextHolder.clearContext();

            LOGGER.debug("Authentication request failed: " + failed);

            eventPublisher.publishAuthenticationFailure(new BadCredentialsException(failed.getMessage(), failed),
                    new PreAuthenticatedAuthenticationToken("access-token", "N/A"));

            authenticationEntryPoint.commence(request, response,
                    new InsufficientAuthenticationException(failed.getMessage(), failed));

            return;
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
            throw new InvalidTokenException(exceptionService.convertMessage("message.authentication.loadAuthentication.null"));
        } else {
            if (!redisService.verifyTokenInRedisServer(auth.getName(),request.getRemoteAddr(),token)) {
                throw new InvalidTokenException(exceptionService.convertMessage("message.user.notFoundInRedis"));
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
            throw new InvalidTokenException(exceptionService.convertMessage("message.authentication.loadAuthentication.null"));
        } else if (token.isExpired()) {
            authentication = tokenStore.readAuthentication(accessToken);
            boolean tokenRemoved = removeTokenFromRedis(authentication.getUserAuthentication().getName(), request.getRemoteAddr(),accessToken);
            if (!tokenRemoved) {
                throw new InvalidTokenException(exceptionService.convertMessage("unable to removed expired token from redis"));
            }
            tokenStore.readAccessToken(accessToken);
            throw new InvalidTokenException(exceptionService.convertMessage("message.token.expire"));
        }
        authentication = tokenStore.readAuthentication(accessToken);
        if (authentication == null) {
            throw new InvalidTokenException(exceptionService.convertMessage("message.token.expired"));
        }
        return authentication;
    }


    @Override
    public void afterPropertiesSet() {

        // no need to define
    }

    private static final class NullEventPublisher implements AuthenticationEventPublisher {
        public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
        }

        public void publishAuthenticationSuccess(Authentication authentication) {
        }
    }
    private boolean removeTokenFromRedis(String userName, String clientIp,String accessToken) {
        return redisService.removeUserTokenFromRedisByClientIpAddress(userName, clientIp,accessToken);
    }

}
