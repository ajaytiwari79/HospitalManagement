package com.kairos.dto.activity.unit_settings.activity_configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigInteger;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityConfigurationDTO {
    private BigInteger id;
    private Long unitId;
    private PresencePlannedTime presencePlannedTime;
    private AbsencePlannedTime absencePlannedTime;
    @Indexed
    private Long countryId;

    public ActivityConfigurationDTO() {
        //dc
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public PresencePlannedTime getPresencePlannedTime() {
        return presencePlannedTime;
    }

    public void setPresencePlannedTime(PresencePlannedTime presencePlannedTime) {
        this.presencePlannedTime = presencePlannedTime;
    }

    public AbsencePlannedTime getAbsencePlannedTime() {
        return absencePlannedTime;
    }

    public void setAbsencePlannedTime(AbsencePlannedTime absencePlannedTime) {
        this.absencePlannedTime = absencePlannedTime;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }
}

