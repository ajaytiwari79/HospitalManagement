package com.kairos.persistence.model.activity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ActivityWrapper {
    private Activity activity;
    private String timeType;
    private TimeType timeTypeInfo;
    private ActivityPriority activityPriority;

    public ActivityWrapper(Activity activity, String timeType) {
        this.activity = activity;
        this.timeType = timeType;
    }

    public ActivityWrapper(Activity activity, String timeType, TimeType timeTypeInfo) {
        this.activity = activity;
        this.timeType = timeType;
        this.timeTypeInfo = timeTypeInfo;
    }
}
