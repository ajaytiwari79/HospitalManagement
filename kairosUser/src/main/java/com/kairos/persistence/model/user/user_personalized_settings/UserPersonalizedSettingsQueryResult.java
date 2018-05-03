package com.kairos.persistence.model.user.user_personalized_settings;

import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by yatharth on 2/5/18.
 */
@QueryResult
public class UserPersonalizedSettingsQueryResult {
   private UserPersonalizedSettings userPersonalizedSettings;

    public SelfRosteringView getSelfRosteringView() {
        return selfRosteringView;
    }

    public void setSelfRosteringView(SelfRosteringView selfRosteringView) {
        this.selfRosteringView = selfRosteringView;
    }

    private SelfRosteringView selfRosteringView;

    public UserPersonalizedSettings getUserPersonalizedSettings() {
        return userPersonalizedSettings;
    }

    public void setUserPersonalizedSettings(UserPersonalizedSettings userPersonalizedSettings) {
        this.userPersonalizedSettings = userPersonalizedSettings;
    }
}
