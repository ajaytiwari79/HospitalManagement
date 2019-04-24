package com.planner.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class ResponseFilter implements Filter {

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = asHttp(request);
		HttpServletResponse httpResponse = asHttp(response);
		httpResponse.setHeader("Access-Control-Allow-Origin", "*");
		httpResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE,PUT");
		httpResponse.setHeader("Access-Control-Max-Age", "3600");
		httpResponse.setHeader("Access-Control-Allow-Headers", "x-requested-with,Authorization, Content-Type");
		httpResponse.setHeader("Content-Type", "application/json");
		chain.doFilter(request, response);
	}

	private HttpServletRequest asHttp(ServletRequest request) {
		return (HttpServletRequest) request;
	}

	private HttpServletResponse asHttp(ServletResponse response) {
		return (HttpServletResponse) response;
	}

	@Override
	public void destroy() {

	}

}
