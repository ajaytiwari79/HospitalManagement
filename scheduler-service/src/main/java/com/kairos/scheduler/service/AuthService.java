package com.kairos.scheduler.service;

import com.kairos.scheduler.utils.BeanFactoryUtil;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

@Service
public class AuthService {

    @Inject
    private UserIntegrationService userIntegrationService;


    public String getAuthToken() {

        String authToken;
        if(BeanFactoryUtil.getDefaultListableBeanFactory().containsBean("authToken")) {
            authToken = BeanFactoryUtil.getDefaultListableBeanFactory()
                    .getBean("authToken", String.class);
            return authToken;

        }
        else {
                authToken = userIntegrationService.getAuthToken();
                BeanFactoryUtil.registerSingleton("authToken",authToken);
            }
        return authToken;
    }
    public String getNewAuthToken() {
        String authToken = userIntegrationService.getAuthToken();
        BeanFactoryUtil.getDefaultListableBeanFactory().destroySingleton("authToken");
        BeanFactoryUtil.registerSingleton("authToken",authToken);
        return authToken;
    }


}
