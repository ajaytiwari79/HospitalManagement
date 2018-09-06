package com.kairos.config.security;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Its a duplicate filter with CORSFilter and got created as it was not known.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CORSFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
    }
    private static final String ALLOWED_HEADERS = "X-Requested-With,access-control-allow-origin,Authorization,authorization,Origin,Content-Type,Version";
    private static final String ALLOWED_METHODS = "GET,PUT,POST,DELETE,OPTIONS";
    private static final String ALLOWED_ORIGIN = "*";
    private static final String MAX_AGE = "3600";
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
            HttpServletResponse resp=(HttpServletResponse)response;
            resp.addHeader("Access-Control-Allow-Origin",ALLOWED_ORIGIN);
            resp.addHeader("Access-Control-Allow-Credentials","true");
            resp.addHeader("Access-Control-Allow-Methods",ALLOWED_METHODS);
            resp.addHeader("Access-Control-Allow-Headers",ALLOWED_HEADERS);
            resp.addHeader("Access-Control-Max-Age", MAX_AGE);
            if(((HttpServletRequest)request).getMethod().equals("OPTIONS")){
                return;
            }
            chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
