package com.kairos.activity.persistence.model.activity.tabs;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by pawanmandhan on 22/8/17.
 */
public class BalanceSettingsActivityTab implements Serializable{


    private Integer addTimeTo;
    private String addDayTo;
    private Long presenceTypeId;
    private BigInteger timeTypeId;;
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

    public BalanceSettingsActivityTab(Integer addTimeTo, String addDayTo,BigInteger timeTypeId, Long presenceTypeId, Boolean onCallTimePresentPresent, Boolean negativeDayBalancePresent) {
        this.addTimeTo = addTimeTo;
        this.addDayTo = addDayTo;
        this.timeTypeId = timeTypeId;
        this.presenceTypeId = presenceTypeId;
        this.onCallTimePresent = onCallTimePresentPresent;
        this.negativeDayBalancePresent = negativeDayBalancePresent;
    }

    public Long getPresenceTypeId() {
        return presenceTypeId;
    }

    public void setPresenceTypeId(Long presenceTypeId) {
        this.presenceTypeId = presenceTypeId;
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

    public String getAddDayTo() {
        return addDayTo;
    }

    public void setAddDayTo(String addDayTo) {
        this.addDayTo = addDayTo;
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
}
