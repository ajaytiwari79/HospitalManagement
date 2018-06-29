package com.kairos.user.user.user_personalized_settings;

/**
 * Created by yatharth on 1/5/18.
 */
public class UserPersonalizedSettingsDto {
    public SelfRosteringViewDto getSelfRosteringView() {
        return selfRosteringView;
    }

    public void setSelfRosteringView(SelfRosteringViewDto selfRosteringView) {
        this.selfRosteringView = selfRosteringView;
    }

    private SelfRosteringViewDto selfRosteringView;

    public UserPersonalizedSettingsDto() {

    }
    public UserPersonalizedSettingsDto(SelfRosteringViewDto selfRosteringView) {
        this.selfRosteringView = selfRosteringView;
    }
}
