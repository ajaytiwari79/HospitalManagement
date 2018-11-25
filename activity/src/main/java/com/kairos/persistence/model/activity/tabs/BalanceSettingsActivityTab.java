package com.kairos.persistence.model.activity.tabs;

import com.kairos.enums.TimeTypeEnum;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by pawanmandhan on 22/8/17.
 */
public class BalanceSettingsActivityTab implements Serializable{


    private Integer addTimeTo;
    private BigInteger timeTypeId;
    private TimeTypeEnum timeType; // This is used to verify the activity is of  paid break or unpaid break
    private boolean onCallTimePresent ;
    private Boolean negativeDayBalancePresent;

    public BalanceSettingsActivityTab() {
    }

    public BalanceSettingsActivityTab(BigInteger timeTypeId) {
        this.timeTypeId = timeTypeId;
    }

    public BalanceSettingsActivityTab(boolean onCallTimePresent, Boolean negativeDayBalancePresent) {
        this.onCallTimePresent = onCallTimePresent;
        this.negativeDayBalancePresent = negativeDayBalancePresent;
    }

    public BalanceSettingsActivityTab(Integer addTimeTo, BigInteger timeTypeId, Boolean onCallTimePresentPresent, Boolean negativeDayBalancePresent) {
        this.addTimeTo = addTimeTo;
        this.timeTypeId = timeTypeId;
        this.onCallTimePresent = onCallTimePresentPresent;
        this.negativeDayBalancePresent = negativeDayBalancePresent;
    }

    public void setOnCallTimePresent(boolean onCallTimePresent) {
        this.onCallTimePresent = onCallTimePresent;
    }

    public Integer getAddTimeTo() {
        return addTimeTo;
    }

    public void setAddTimeTo(Integer addTimeTo) {
        this.addTimeTo = addTimeTo;
    }

    public Boolean getOnCallTimePresent() {
        return onCallTimePresent;
    }

    public Boolean getNegativeDayBalancePresent() {
        return negativeDayBalancePresent;
    }

    public BigInteger getTimeTypeId() {
        return timeTypeId;
    }

    public void setTimeTypeId(BigInteger timeTypeId) {
        this.timeTypeId = timeTypeId;
    }

    public Boolean isOnCallTimePresent() {
        return onCallTimePresent;
    }

    public void setOnCallTimePresent(Boolean onCallTimePresent) {
        this.onCallTimePresent = onCallTimePresent;
    }

    public Boolean isNegativeDayBalancePresent() {
        return negativeDayBalancePresent;
    }

    public void setNegativeDayBalancePresent(Boolean negativeDayBalancePresent) {
        this.negativeDayBalancePresent = negativeDayBalancePresent;
    }

    public TimeTypeEnum getTimeType() {
        return timeType;
    }

    public void setTimeType(TimeTypeEnum timeType) {
        this.timeType = timeType;
    }
}
