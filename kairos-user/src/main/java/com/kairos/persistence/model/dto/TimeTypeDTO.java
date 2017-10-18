package com.kairos.persistence.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.country.TimeType;

/**
 * Created by vipul on 17/10/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeTypeDTO extends UserBaseEntity {
    private String name;
    private String type;
    private boolean includeInTimeBank = true;
    private Long countryId;
    private boolean enabled = true;
    private Boolean negativeDayBalancePresent;
    private Boolean onCallTime;

    public Boolean getOnCallTime() {
        return onCallTime;
    }

    public void setOnCallTime(Boolean onCallTime) {
        this.onCallTime = onCallTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isIncludeInTimeBank() {
        return includeInTimeBank;
    }

    public void setIncludeInTimeBank(boolean includeInTimeBank) {
        this.includeInTimeBank = includeInTimeBank;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getNegativeDayBalancePresent() {
        return negativeDayBalancePresent;
    }

    public void setNegativeDayBalancePresent(Boolean negativeDayBalancePresent) {
        this.negativeDayBalancePresent = negativeDayBalancePresent;
    }



    public TimeTypeDTO(String name, String type, boolean includeInTimeBank, Long countryId, boolean enabled, Boolean negativeDayBalancePresent, Boolean onCallTime) {
        this.name = name;
        this.type = type;
        this.includeInTimeBank = includeInTimeBank;
        this.countryId = countryId;
        this.enabled = enabled;
        this.negativeDayBalancePresent = negativeDayBalancePresent;
        this.onCallTime = onCallTime;
    }

    public TimeType buildTimeType() {
        TimeType timeType = new TimeType(this.name, this.type, this.includeInTimeBank, this.enabled, this.negativeDayBalancePresent, this.onCallTime);
        return timeType;
    }

}
