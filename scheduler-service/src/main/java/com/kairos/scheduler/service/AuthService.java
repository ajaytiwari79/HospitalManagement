package com.kairos.scheduler.service;

import com.kairos.scheduler.utils.BeanFactoryUtil;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

@Service
public class AuthService {


    public String getAuthToken() {

            String authToken = BeanFactoryUtil.getDefaultListableBeanFactory()
                    .getBean("authToken", String.class);

            if(Optional.ofNullable(authToken).isPresent()) {


            }
        return authToken;
    }


}
