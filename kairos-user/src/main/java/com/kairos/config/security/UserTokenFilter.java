package com.kairos.config.security;
import com.kairos.persistence.model.user.auth.User;
import com.kairos.persistence.model.user.auth.UserAuthentication;
import com.kairos.service.auth.UserService;
import com.kairos.util.response.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.kairos.constants.ApiConstants.API_V1;
import static com.kairos.constants.AppConstants.*;

/**
 * Created by neuron on 24/5/17.
 */
public class UserTokenFilter extends GenericFilterBean {



    private UserService userService;

    public UserTokenFilter(UserService userService){
        this.userService = userService;
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE,PUT");
        httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Origin,authToken,Content-Type,Accept");

        if(httpServletRequest.getMethod().equals("OPTIONS")){
            httpServletResponse.setStatus(HttpServletResponse.SC_ACCEPTED);
            return;
        }


        String requestUri = httpServletRequest.getRequestURI();
        if (    requestUri.equals(API_LOGIN_URL)||
                requestUri.equals(API_LOGOUT_URL) ||
                requestUri.equals(API_LOGIN_MOBILE_URL) ||
                requestUri.equals(API_VERIFY_OTP) ||
                requestUri.equals(SWAGGER_API_DOCS)||
                requestUri.contains(SWAGGER_UI_PATH) ||
                requestUri.contains(API_COUNTRY_CODE_LIST) ||
                requestUri.contains(API_LOGIN_MOBILE_NUMBER_URL) ||
                requestUri.contains(WEBJARS) ||
                requestUri.contains(API_TIME_CARE_SHIFTS) ||
                requestUri.contains(API_TIME_CARE_ACTIVITIES) ||
                requestUri.contains(API_KMD_CARE_CITIZEN) ||
                requestUri.contains(API_KMD_CARE_CITIZEN_GRANTS) ||
                requestUri.contains(API_KMD_CARE_CITIZEN_RELATIVE_DATA) ||
                requestUri.contains(API_CREDENTIAL_UPDATE_URL) ||
                requestUri.contains("/api/v1/aggregator/citizen") ||
                requestUri.contains("/generateExcels/citizenFile") ||
                requestUri.contains(SWAGGER_RESOURCES) ||
                requestUri.contains("springfox-swagger-ui") ||
                requestUri.contains(API_V1+"/messaging/manage/pushToQueue") ||
                requestUri.contains(API_V1+"/kairos/ws") ||
                requestUri.contains("monitoring") ||
                requestUri.contains("health")){
            chain.doFilter(request, response);
        }
        else {
            User user = userService.findByAccessToken(httpServletRequest.getHeader("authToken"));
            if (user != null) {
                UserAuthentication userAuthentication = new UserAuthentication(user);
                userAuthentication.setAuthenticated(true);
                SecurityContextHolder.getContext().setAuthentication(userAuthentication);
                chain.doFilter(request, response);
            } else {
                httpServletResponse.setCharacterEncoding("UTF-8");
                httpServletResponse.setContentType(CONTENT_TYPE_JSON);
                String json = ResponseHandler.generateResponse(HttpStatus.UNAUTHORIZED,false,null).toString();
                httpServletResponse.getWriter().write(json);
                httpServletResponse.sendError(401);
            }
        }
    }
}
