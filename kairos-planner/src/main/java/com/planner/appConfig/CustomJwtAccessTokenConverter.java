package com.planner.appConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CustomJwtAccessTokenConverter  {//extends JwtAccessTokenConverter
    /*private static final Logger log = LoggerFactory.getLogger(CustomJwtAccessTokenConverter.class);

    private static final String USER_DETAILS_KEY = "details";

    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
        log.debug("extractAuthentication additional information {} from token");
        final OAuth2Authentication authentication =    super.extractAuthentication(map);
         Map<String, Object> additionalInfo = new HashMap<>();
          ObjectMapper mapper=new ObjectMapper();
         //CurrentUserDetails details=mapper.convertValue(map.get(USER_DETAILS_KEY),CurrentUserDetails.class);
         *//*authentication.setDetails(details);
         UserContext.setUserDetails(details);
         *//*return authentication;
    }*/
}
