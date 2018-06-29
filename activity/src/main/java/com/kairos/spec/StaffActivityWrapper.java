package com.kairos.spec;

import java.util.Date;
import java.util.List;

/**
 * Created by oodles on 29/11/17.
 */
public class StaffActivityWrapper {

    private List<Long> skills;
    private Date activityStartDateTime;

    public StaffActivityWrapper() {
    }

    public List<Long> getSkills() {
        return skills;
    }

    public void setSkills(List<Long> skills) {
        this.skills = skills;
    }

    public Date getActivityStartDateTime() {
        return activityStartDateTime;
    }

    public void setActivityStartDateTime(Date activityStartDateTime) {
        this.activityStartDateTime = activityStartDateTime;
    }
}
