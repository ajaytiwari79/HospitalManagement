package com.kairos.activity.client.dto.Phase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.persistence.model.phase.Phase;
import com.kairos.activity.service.phase.DurationType;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Created by vipul on 19/9/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhaseDTO {
    private BigInteger id;

    @NotNull(message = "error.phase.name.notnull")
    private String name;
    private String description;
    @Range(min = 0)
    private int duration;
    private DurationType durationType;
    private int sequence;
    @Indexed
    private Long organizationId;
    private Long countryId;
    private BigInteger parentCountryPhaseId;
    private boolean allowFlipping;
    private LocalTime flippingTime;
    private DayOfWeek flippingDay;


    public long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(long organizationId) {
        this.organizationId = organizationId;
    }

    public PhaseDTO() {
        //default cons
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }


    public int getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public DurationType getDurationType() {
        return durationType;
    }

    public void setDurationType(DurationType durationType) {
        this.durationType = durationType;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public boolean isAllowFlipping() {
        return allowFlipping;
    }

    public void setAllowFlipping(boolean allowFlipping) {
        this.allowFlipping = allowFlipping;
    }

    public LocalTime getFlippingTime() {
        return flippingTime;
    }

    public void setFlippingTime(LocalTime flippingTime) {
        this.flippingTime = flippingTime;
    }

    public DayOfWeek getFlippingDay() {
        return flippingDay;
    }

    public void setFlippingDay(DayOfWeek flippingDay) {
        this.flippingDay = flippingDay;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public BigInteger getParentCountryPhaseId() {
        return parentCountryPhaseId;
    }

    public void setParentCountryPhaseId(BigInteger parentCountryPhaseId) {
        this.parentCountryPhaseId = parentCountryPhaseId;
    }

    public PhaseDTO(@NotNull(message = "error.phase.name.notnull") String name, String description, @Range(min = 0) int duration, DurationType durationType, int sequence, Long countryId) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.durationType = durationType;
        this.sequence = sequence;
        this.countryId = countryId;
    }

    public PhaseWeeklyDTO buildWeekDTO() {
        PhaseWeeklyDTO phaseWeeklyDTO = new PhaseWeeklyDTO(id, name, description, duration, sequence, organizationId);
        return phaseWeeklyDTO;
    }

    public Phase buildPhaseForCountry() {
        Phase phase = new Phase(this.name, this.description, this.duration, this.durationType, this.sequence, this.countryId, this.allowFlipping, this.flippingTime, this.flippingDay, this.organizationId, this.parentCountryPhaseId);
        return phase;
    }

}
