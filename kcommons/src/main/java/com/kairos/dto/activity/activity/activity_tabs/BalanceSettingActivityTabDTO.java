package com.kairos.dto.activity.activity.activity_tabs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.TimeTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigInteger;

/**
 * Created by pawanmandhan on 22/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@AllArgsConstructor
public class BalanceSettingActivityTabDTO {


    private Long activityId;
    private Integer addTimeTo;
    private BigInteger timeTypeId;
    private TimeTypeEnum timeType; // This is used to verify the activity is of  paid break or unpaid break
    private boolean onCallTimePresent;
    private Boolean negativeDayBalancePresent;

    public BalanceSettingActivityTabDTO() {
    }

    public BalanceSettingActivityTabDTO(BigInteger timeTypeId) {
        this.timeTypeId = timeTypeId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Boolean getOnCallTimePresent() {
        return onCallTimePresent;
    }

    public void setOnCallTimePresent(Boolean onCallTimePresent) {
        this.onCallTimePresent = onCallTimePresent;
    }

    public Boolean getNegativeDayBalancePresent() {
        return negativeDayBalancePresent;
    }

    public void setNegativeDayBalancePresent(Boolean negativeDayBalancePresent) {
        this.negativeDayBalancePresent = negativeDayBalancePresent;
    }

    public Integer getAddTimeTo() {
        return addTimeTo;
    }

    public void setAddTimeTo(Integer addTimeTo) {
        this.addTimeTo = addTimeTo;
    }

    public BigInteger getTimeTypeId() {
        return timeTypeId;
    }

    public void setTimeTypeId(BigInteger timeTypeId) {
        this.timeTypeId = timeTypeId;
    }

    public boolean isOnCallTimePresent() {
        return onCallTimePresent;
    }

    public void setOnCallTimePresent(boolean onCallTimePresent) {
        this.onCallTimePresent = onCallTimePresent;
    }

    public TimeTypeEnum getTimeType() {
        return timeType;
    }

    public void setTimeType(TimeTypeEnum timeType) {
        this.timeType = timeType;
    }
}
