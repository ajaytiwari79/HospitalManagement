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
        UserContext.setCorrelationId(  httpServletRequest.getHeader(UserContext.CORRELATION_ID) );
        UserContext.setUserId( httpServletRequest.getHeader(UserContext.USER_ID) );
        UserContext.setAuthToken( httpServletRequest.getHeader(UserContext.AUTH_TOKEN) );
        long time = System.currentTimeMillis();
        try {
            filterChain.doFilter(httpServletRequest, servletResponse);
        } finally {
            time = System.currentTimeMillis() - time;
            logger.info("time taken {}: {} ms ", httpServletRequest.getRequestURI(),  time);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}