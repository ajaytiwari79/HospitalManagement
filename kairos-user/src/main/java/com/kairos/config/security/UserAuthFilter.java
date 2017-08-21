package com.kairos.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kairos.persistence.model.user.auth.UserAuthentication;
import com.kairos.service.auth.UserService;
import com.kairos.utils.response.ResponseHandler;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by neuron on 24/5/17.
 */
@WebFilter
public class UserAuthFilter extends AbstractAuthenticationProcessingFilter{

    Logger logger = LoggerFactory.getLogger(UserAuthFilter.class);

    public UserAuthFilter(){
        super("/api/v1/login/auth");
    }


    @Inject
    UserService userService;

    protected UserAuthFilter(String defaultFilterProcessesUrl,UserService userService) {
        super(defaultFilterProcessesUrl);
        this.userService = userService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {


        String username = request.getParameter("userName");
        String password = request.getParameter("password");
        logger.info("trying to login"+username);
        UserAuthentication auth = userService.authenticateUser(username, password);
        if(auth!=null){
            auth.setAuthenticated(true);
            return auth;
        }else {
            throw new CustomAuthException("Not Authorized");
        }
    }



    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            javax.servlet.FilterChain chain, Authentication authResult) throws IOException {

        UserAuthentication userAuthentication = (UserAuthentication) authResult;

            SecurityContextHolder.getContext().setAuthentication(userAuthentication);
            logger.info("user is authenticated");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE,PUT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Origin,authtoken,Content-Type,Accept");

        if(request.getMethod().equals("OPTIONS")){
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
            return;
        }
        response.setStatus(HttpServletResponse.SC_OK);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("authToken",UserAuthentication.getCurrentUser().getAccessToken());
        jsonObject.put("message","success");
        response.getWriter().write(ResponseHandler.generateResponse(jsonObject).getBody());

    }

    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {

        logger.info("user is not authorized"+failed.getMessage());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message","login failed");
        response.getWriter().write(ResponseHandler.generateResponse(jsonObject).getBody());
    }

    @Override
    @Inject
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    private HttpServletResponse asHttp(ServletResponse response) {
        return (HttpServletResponse) response;
    }
}
