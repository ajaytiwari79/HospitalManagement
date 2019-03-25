package com.kairos.commons.service;

import com.kairos.commons.config.EnvConfigCommon;
import com.kairos.commons.utils.BeanFactoryUtil;
import com.kairos.commons.utils.UserRestClientAuth;
import com.kairos.constants.CommonConstants;
import com.kairos.enums.IntegrationOperation;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.inject.Inject;
import java.util.Map;

@Service
public class TokenAuthService {

@Inject
private EnvConfigCommon envConfigCommon;
@Inject
private UserRestClientAuth userRestClientAuth;

    public String getAuthToken() {

        String authToken;
        if(BeanFactoryUtil.getDefaultListableBeanFactory().containsBean(CommonConstants.AUTH_TOKEN)) {
            authToken = BeanFactoryUtil.getDefaultListableBeanFactory()
                    .getBean(CommonConstants.AUTH_TOKEN, String.class);
            return authToken;

        }
        else {
                authToken = getAuthTokenFromUser();
                BeanFactoryUtil.registerSingleton(CommonConstants.AUTH_TOKEN,authToken);
            }
        return authToken;
    }

    public String getNewAuthToken() {
        String authToken = getAuthTokenFromUser();
        BeanFactoryUtil.getDefaultListableBeanFactory().destroySingleton(CommonConstants.AUTH_TOKEN);
        BeanFactoryUtil.registerSingleton(CommonConstants.AUTH_TOKEN,authToken);
        return authToken;
    }

    public String getAuthTokenFromUser() {
        MultiValueMap<String,String> formParameters = new LinkedMultiValueMap<String,String>();
        formParameters.add("username", envConfigCommon.getUserServiceAuthUsername());
        formParameters.add("password", envConfigCommon.getUserServiceAuthPassword());
        formParameters.add("grant_type","password");
        Map<String,Object> accessTokenMap = userRestClientAuth.publishRequest(null,false,IntegrationOperation.CREATE,formParameters);
        if(accessTokenMap.containsKey("access_token")) {
            return (String)accessTokenMap.get("access_token");
        }
        else {
            return null;
        }
    }


}