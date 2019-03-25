package com.kairos.persistence.model.activity;

public class ActivityWrapper {
    private Activity activity;
    private String timeType;
    private TimeType timeTypeInfo;


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

    public TimeType getTimeTypeInfo() {
        return timeTypeInfo;
    }

    public void setTimeTypeInfo(TimeType timeTypeInfo) {
        this.timeTypeInfo = timeTypeInfo;
    }
}
