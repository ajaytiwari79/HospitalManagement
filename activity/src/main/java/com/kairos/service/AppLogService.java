package com.kairos.service;

import com.kairos.service.logging.AppLog;
import com.kairos.commons.utils.DateUtils;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by oodles on 7/6/17.
 */
@Service
public class AppLogService extends MongoBaseService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AppLogService.class);

    public AppLog persistLog(String activityLog){
       /* if(SecurityContextHolder.getContext() != null || SecurityContextHolder.getContext().getAuthentication() != null) return null;
        if(SecurityContextHolder.getContext().getAuthentication().getDetails() != null) return null;
        String json = JsonUtils.toJSON(SecurityContextHolder.getContext().getAuthentication().getDetails());
        UserPojo user = JsonUtils.toObject(json, UserPojo.class);*/
       /* AppLog appLog = new AppLog();
        if(user.getId() == null){
            appLog.setUserName(AppConstants.DATA_SAVED_FROM_SERVICE);
        }else{
            appLog.setUserId(user.getId());
            appLog.setUserName(user.getFirstName()+" "+user.getLastName());
        }*/
       // appLog.setActivityLog(activityLog);
       // return logging(appLog);
        AppLog appLog = new AppLog();
        return appLog;
    }

    public AppLog logging(AppLog appLog){

       // Assert.notNull(appLog, "Entity must not be null!");

        //Class name for sequence
        String className = appLog.getClass().getSimpleName();

        //  Set Id if entity don't have Id
        if(appLog.getId() == null){
            appLog.setId(mongoSequenceRepository.nextSequence(className));
        }

        //  Set createdAt if entity don't have createdAt
        if(appLog.getCreatedAt() == null){
            appLog.setCreatedAt(DateUtils.getDate());
        }

       //Set updatedAt time as current time
        appLog.setUpdatedAt(DateUtils.getDate());
        mongoTemplate.save(appLog);
        return appLog;
    }

}
