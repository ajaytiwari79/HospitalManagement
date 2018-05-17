package com.kairos.activity.persistence.model.activity.tabs;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.persistence.model.common.MongoBaseEntity;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

/**
 * Created by pawanmandhan on 22/8/17.
 */

@Document(collection = "activity_category")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityCategory extends MongoBaseEntity{

    private String name;
    private String description;
    private Long countryId;
    private Long unitId;


    public ActivityCategory() {
        //Default Constructor
    }

    public ActivityCategory(BigInteger id, String name) {
        setId(id);
        this.name = name;
    }

    public ActivityCategory(String name, String description, Long countryId) {
        this.name = name;
        this.description = description;
        this.countryId=countryId;
    }


    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ActivityCategory)) return false;

        ActivityCategory that = (ActivityCategory) o;

        return new EqualsBuilder()
                .append(name, that.name)
                .append(this.getId(), that.getId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(this.getId())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id",this.getId())
                .append("name", name)
                .append("description", description)
                .toString();
    }
}
