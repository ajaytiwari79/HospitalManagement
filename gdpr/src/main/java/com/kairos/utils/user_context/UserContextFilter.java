package com.kairos.utils.user_context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class UserContextFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(UserContextFilter.class);
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        logger.info("I am entering the gdpr service id with auth token: {}", httpServletRequest.getHeader("Authorization"));
        UserContext.setCorrelationId(  httpServletRequest.getHeader(UserContext.CORRELATION_ID) );
        UserContext.setUserId( httpServletRequest.getHeader(UserContext.USER_ID) );
        UserContext.setAuthToken( httpServletRequest.getHeader(UserContext.AUTH_TOKEN) );
        logger.debug("Exiting the UserContextFilter");
        filterChain.doFilter(httpServletRequest, servletResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}
}