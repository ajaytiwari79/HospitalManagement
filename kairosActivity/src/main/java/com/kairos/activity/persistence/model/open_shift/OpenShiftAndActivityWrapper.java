package com.kairos.activity.persistence.model.open_shift;

import com.kairos.activity.persistence.model.activity.Activity;

public class OpenShiftAndActivityWrapper {
    private OpenShift openShift;
    private Activity activity;
    private Long expertiseId;

    public OpenShiftAndActivityWrapper() {
        //Default Constructor
    }

    public OpenShiftAndActivityWrapper(OpenShift openShift, Activity activity, Long expertiseId) {
        this.openShift = openShift;
        this.activity = activity;
        this.expertiseId = expertiseId;
    }

    public OpenShift getOpenShift() {
        return openShift;
    }

    public void setOpenShift(OpenShift openShift) {
        this.openShift = openShift;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Long getExpertiseId() {
        return expertiseId;
    }

    public void setExpertiseId(Long expertiseId) {
        this.expertiseId = expertiseId;
    }
}
