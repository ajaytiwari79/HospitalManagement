package com.kairos.activity.service.logging;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;

/**
 * Created by neuron on 11/11/16.
 */
public class AppLog extends MongoBaseEntity {


        String activityLog;
        long userId;
        String userName;


        public AppLog(){

        }

        public AppLog(String activityLog,long userId,String userName){
            this.activityLog = activityLog;
            this.userId = userId;
            this.userName = userName;
        }

        public String toString(){
            StringBuilder sb= new StringBuilder();
            return sb.append("User with id ").append(userId).append(" and name ").append(userName)
                    .append(" activity log ").append(activityLog).toString();
        }

    public String getActivityLog() {
        return activityLog;
    }

    public void setActivityLog(String activityLog) {
        this.activityLog = activityLog;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
