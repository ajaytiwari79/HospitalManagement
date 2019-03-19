package com.kairos.persistence.model.activity;

public class ActivityWrapper {
    private Activity activity;
    private String timeType;
    private TimeType timeTypeData;


    public ActivityWrapper() {
    }

    public ActivityWrapper(Activity activity, String timeType) {
        this.activity = activity;
        this.timeType = timeType;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public String getTimeType() {
        return timeType;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }

    public TimeType getTimeTypeData() {
        return timeTypeData;
    }

    public void setTimeTypeData(TimeType timeTypeData) {
        this.timeTypeData = timeTypeData;
    }
}
