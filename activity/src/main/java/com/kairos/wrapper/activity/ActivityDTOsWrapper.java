package com.kairos.wrapper.activity;

import com.kairos.dto.activity.activity.activity_tabs.GeneralActivityTabWithTagDTO;
import com.kairos.persistence.model.activity.tabs.ActivityCategory;

import java.math.BigInteger;
import java.util.List;

//Custom wrapper having those Dto which have modified field changes.
public class ActivityDTOsWrapper {
    private BigInteger activityId;
    private GeneralActivityTabWithTagDTO generalTab;
    private List<ActivityCategory> activityCategories;

    //============================================================

    public ActivityDTOsWrapper() {
    }

    public ActivityDTOsWrapper(BigInteger activityId, GeneralActivityTabWithTagDTO generalTab, List<ActivityCategory> activityCategories) {
        this.activityId = activityId;
        this.generalTab = generalTab;
        this.activityCategories = activityCategories;
    }

//============================================================

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public GeneralActivityTabWithTagDTO getGeneralTab() {
        return generalTab;
    }

    public void setGeneralTab(GeneralActivityTabWithTagDTO generalTab) {
        this.generalTab = generalTab;
    }

    public List<ActivityCategory> getActivityCategories() {
        return activityCategories;
    }

    public void setActivityCategories(List<ActivityCategory> activityCategories) {
        this.activityCategories = activityCategories;
    }
}
