package com.kairos.persistence.model.activity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class ActivityWrapper implements Serializable {
    private static final long serialVersionUID = -1555089459872201303L;
    private Activity activity;
    private String timeType;
    private TimeType timeTypeInfo;
    private Integer ranking;

    public ActivityWrapper(Activity activity, String timeType) {
        this.activity = activity;
        this.timeType = timeType;
    }

    public ActivityWrapper(Activity activity, String timeType, TimeType timeTypeInfo) {
        this.activity = activity;
        this.timeType = timeType;
        this.timeTypeInfo = timeTypeInfo;
    }

    public ActivityWrapper(Activity activity) {
        this.activity = activity;
    }
}
