package com.kairos.persistence.model.user.country;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

/**
 * Created by vipul on 17/10/17.
 */
@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeType extends UserBaseEntity {

    private String name;
    private String type;
    private boolean includeInTimeBank = true;
    @Relationship(type = BELONGS_TO , direction=Relationship.OUTGOING)
    private Country country;
    private boolean deleted;
    private boolean negativeDayBalancePresent;
    private boolean onCallTime;

    public TimeType() {
    }

    public TimeType(String name, String type, boolean includeInTimeBank, Country country, boolean deleted, boolean negativeDayBalancePresent, boolean onCallTime) {
        this.name = name;
        this.type = type;
        this.includeInTimeBank = includeInTimeBank;
        this.country = country;
        this.deleted = deleted;
        this.negativeDayBalancePresent = negativeDayBalancePresent;
        this.onCallTime = onCallTime;
    }

    public TimeType(String name, String type, boolean includeInTimeBank, boolean deleted, boolean negativeDayBalancePresent, boolean onCallTime) {
        this.name = name;
        this.type = type;
        this.includeInTimeBank = includeInTimeBank;
        this.deleted = deleted;
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
