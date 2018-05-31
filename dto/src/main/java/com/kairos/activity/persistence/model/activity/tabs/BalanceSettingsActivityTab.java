package com.kairos.activity.persistence.model.activity.tabs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.response.dto.web.cta.TimeTypeDTO;

import java.math.BigInteger;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BalanceSettingsActivityTab {
    private Integer addTimeTo;
    private String addDayTo;
    private Long presenceTypeId;
    private BigInteger timeTypeId;;
    private boolean onCallTimePresent ;
    private Boolean negativeDayBalancePresent;

    public BalanceSettingsActivityTab() {
        //Default Constructor
    }

    public Integer getAddTimeTo() {
        return addTimeTo;
    }

    public void setAddTimeTo(Integer addTimeTo) {
        this.addTimeTo = addTimeTo;
    }

    public String getAddDayTo() {
        return addDayTo;
    }

    public void setAddDayTo(String addDayTo) {
        this.addDayTo = addDayTo;
    }

    public Long getPresenceTypeId() {
        return presenceTypeId;
    }

    public void setPresenceTypeId(Long presenceTypeId) {
        this.presenceTypeId = presenceTypeId;
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

    public Boolean getNegativeDayBalancePresent() {
        return negativeDayBalancePresent;
    }

    public void setNegativeDayBalancePresent(Boolean negativeDayBalancePresent) {
        this.negativeDayBalancePresent = negativeDayBalancePresent;
    }
}
