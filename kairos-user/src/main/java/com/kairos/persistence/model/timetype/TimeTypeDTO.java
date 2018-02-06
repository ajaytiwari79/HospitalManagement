package com.kairos.persistence.model.timetype;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.country.TimeType;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.NotNull;

/**
 * Created by vipul on 17/10/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@QueryResult
public class TimeTypeDTO extends UserBaseEntity {
    @NotEmpty(message = "error.TimeType.name.notEmpty")    @NotNull(message = "error.TimeType.name.notnull")
    private String name;
    @NotEmpty(message = "error.TimeType.type.notEmpty")     @NotNull(message = "error.TimeType.type.notnull")
    private String type;
    private boolean includeInTimeBank = true;
    private Long countryId;
    @JsonIgnore
    private boolean deleted ;
    private boolean negativeDayBalancePresent;
    private boolean onCallTime;



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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isNegativeDayBalancePresent() {
        return negativeDayBalancePresent;
    }

    public void setNegativeDayBalancePresent(boolean negativeDayBalancePresent) {
        this.negativeDayBalancePresent = negativeDayBalancePresent;
    }

    public boolean isOnCallTime() {
        return onCallTime;
    }

    public void setOnCallTime(boolean onCallTime) {
        this.onCallTime = onCallTime;
    }

    public TimeTypeDTO() {
        //default
    }

    public TimeTypeDTO(String name, String type, boolean includeInTimeBank, Long countryId, boolean deleted, Boolean negativeDayBalancePresent, Boolean onCallTime) {
        this.name = name;
        this.type = type;
        this.includeInTimeBank = includeInTimeBank;
        this.countryId = countryId;
        this.deleted = deleted;
        this.negativeDayBalancePresent = negativeDayBalancePresent;
        this.onCallTime = onCallTime;
    }

    public TimeTypeDTO(String name, String type, boolean includeInTimeBank, boolean deleted, Boolean negativeDayBalancePresent, Boolean onCallTime) {
        this.name = name;
        this.type = type;
        this.includeInTimeBank = includeInTimeBank;
        this.deleted = deleted;
        this.negativeDayBalancePresent = negativeDayBalancePresent;
        this.onCallTime = onCallTime;
    }

    public TimeType buildTimeType() {
        TimeType timeType = new TimeType(this.name, this.type, this.includeInTimeBank, this.deleted, this.negativeDayBalancePresent, this.onCallTime);
        return timeType;
    }

}
