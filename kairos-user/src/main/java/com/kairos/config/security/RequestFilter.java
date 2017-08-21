package com.kairos.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.kairos.persistence.model.user.auth.User;
import com.kairos.persistence.model.user.auth.UserAuthentication;
import com.kairos.service.auth.UserService;
import com.kairos.utils.response.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;
import static com.kairos.constants.AppConstants.*;


/*
 * Filter for Application Requests.
 * 1.Only work for "/api/v1/*" url.
 * 2.Check if Header contains valid access token.
 */
//@Component
//@WebFilter(urlPatterns = {API_V1 +".*"}, description = "Session Checker Filter")
public class RequestFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(RequestFilter.class);
    private static final String AUTH_HEADER_NAME ="authtoken";


    @Inject
    private UserService userService;


    /**
     * Initialization method for filter
     * You can specify FilterConfiguration here
     * @param filterConfig
     * @throws ServletException
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info(filterConfig.getFilterName());
    }




    /**
     *  Casts ServletRequest to HttpServletRequest
     * @param request
     * @return HttpServletRequest
     */
    private HttpServletRequest asHttp(ServletRequest request) {
        return (HttpServletRequest) request;
    }




    /**
     * Casts ServletResponse to HttpServletResponse
     * @param response
     * @return HttpServletResponse
     */
    private HttpServletResponse asHttp(ServletResponse response) {
        return (HttpServletResponse) response;
    }




    /**
     * This method will filter request:
     * 1. Whitelist Swagger documentation URLs.
     * 2. Check if request has access token, else send error response
     * 3. if token is valid , then check if User is allowed to do current operation, else send error response
     *
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpResponse = asHttp(response);
        HttpServletRequest httpRequest = asHttp(request);
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE,PUT");
        httpResponse.setHeader("Access-Control-Max-Age", "3600");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Origin,authtoken,Content-Type,Accept");

        if(httpRequest.getMethod().equals("OPTIONS")){
            httpResponse.setStatus(HttpServletResponse.SC_ACCEPTED);
            return;
        }

        logger.info("URL hit: "+((HttpServletRequest)request).getRequestURL());
        logger.info("Requested Resource: "+((HttpServletRequest)request).getRequestURI());
        logger.info("Request Sender: "+((HttpServletRequest)request).getRemoteUser());
        logger.info("Request IP by X-FORWARDED-FOR: "+((HttpServletRequest)request).getHeader("X-FORWARDED-FOR"));
        logger.info("Request IP by RemoteAdd: "+((HttpServletRequest)request).getRemoteAddr());
        logger.info("===============printing all data from request ");
        Enumeration params = httpRequest.getParameterNames();
        while(params.hasMoreElements()){
            String paramName = (String)params.nextElement();
           logger.info("Parameter Name - "+paramName+", Value - "+request.getParameter(paramName));
        }


        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String token;
        String requestUri = ((HttpServletRequest) request).getRequestURI();

        HttpServletRequest req = (HttpServletRequest) request;
//        Enumeration<String> headerNames = req.getHeaderNames();
//        getHeadersInfo(req);

        // Bypass login, SWAGGER_UI_PATH ,SWAGGER_API_DOCS , SWAGGER_RESOURCES for Token

        if (    requestUri.equals(API_LOGIN_URL)||
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
                requestUri.equals("/") ||
                requestUri.contains("/generateExcels/citizenFile") ||
                requestUri.contains("/api/v1/aggregator/citizen") ||
                requestUri.contains(SWAGGER_RESOURCES) ||
                requestUri.contains(API_V1+"/messaging/manage/pushToQueue") ||
                requestUri.contains(API_V1+"/kairos/ws")){
                chain.doFilter(request, response);
        }
        // Check for token
        else{
            token = req.getHeader(AUTH_HEADER_NAME);
           if (token  != null) {
                logger.info("Token received: " + token + "\n");
                User user = userService.validateUserToken(token);
                if (user != null) {
                    UserAuthentication userAuthentication = new UserAuthentication(user);
                    SecurityContextHolder.getContext().setAuthentication(userAuthentication);
                    req.setAttribute("loggedInUser",user.getFirstName()+" "+user.getLastName());

                    chain.doFilter(request, response);
                }
                // Invalid Token Provided
                else {
                    logger.info("Token is Invalid");
                    String json = ResponseHandler.generateResponse(HttpStatus.UNAUTHORIZED,false,null).toString();
                    httpResponse.sendError(401);
                }
                // No Token Provided
            } else {
                logger.info("No Token Received \n");
                String json = ResponseHandler.generateResponse(HttpStatus.UNAUTHORIZED,false,null).toString();
                response.setContentType(CONTENT_TYPE_JSON);
                response.getOutputStream().write(json.getBytes());
                httpResponse.sendError(401);
            }
        }
    }




    /**
     * Destroy method of filter
     */
    @Override
    public void destroy() {

    }




    /**
     * Prints all header with Values
     * @param request
     * @return Map
     */
    private Map<String, String> getHeadersInfo(HttpServletRequest request) {

        Map<String, String> map = new HashMap<String, String>();

        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
            logger.info(key+" :  "+value+"\n");
        }

        return map;
    }
}
