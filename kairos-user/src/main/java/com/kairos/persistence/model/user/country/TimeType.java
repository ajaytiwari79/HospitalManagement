package com.kairos.persistence.model.user.country;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotNull;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

/**
 * Created by vipul on 17/10/17.
 */
@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeType extends UserBaseEntity {
    @NotEmpty(message = "error.TimeType.name.notEmpty")
    @NotNull(message = "error.TimeType.name.notnull")
    private String name;
    @NotEmpty(message = "error.TimeType.type.notEmpty")
    @NotNull(message = "error.TimeType.type.notnull")
    private String type;
    private boolean includeInTimeBank = true;
    @Relationship(type = BELONGS_TO)
    private Country country;
    private boolean enabled = true;
    private Boolean negativeDayBalancePresent;
    private Boolean onCallTime;

    public TimeType(String name, String type, boolean includeInTimeBank, Country country, boolean enabled, Boolean negativeDayBalancePresent, Boolean onCallTime) {
        this.name = name;
        this.type = type;
        this.includeInTimeBank = includeInTimeBank;
        this.country = country;
        this.enabled = enabled;
        this.negativeDayBalancePresent = negativeDayBalancePresent;
        this.onCallTime = onCallTime;
    }

    public TimeType(String name, String type, boolean includeInTimeBank, boolean enabled, Boolean negativeDayBalancePresent, Boolean onCallTime) {
        this.name = name;
        this.type = type;
        this.includeInTimeBank = includeInTimeBank;
        this.enabled = enabled;
        this.negativeDayBalancePresent = negativeDayBalancePresent;
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

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
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

    public Boolean getOnCallTime() {
        return onCallTime;
    }

    public void setOnCallTime(Boolean onCallTime) {
        this.onCallTime = onCallTime;
    }


    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(this.getId())
                .append(type).hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", this.getId())
                .append("name", name)
                .append("type", type)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof TimeType)) return false;

        TimeType that = (TimeType) o;

        return new EqualsBuilder()
                .append(name, that.name)
                .append(this.getId(), that.getId())
                .isEquals();
    }

}
