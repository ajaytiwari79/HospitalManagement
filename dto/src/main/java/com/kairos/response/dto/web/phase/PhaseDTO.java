package com.kairos.response.dto.web.phase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.phase.PhaseType;
import com.kairos.persistence.model.enums.DurationType;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

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
    private Long organizationId;
    private Long countryId;
    private BigInteger parentCountryPhaseId;
    private int durationInDays;
    private PhaseType phaseType;


    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
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

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public BigInteger getParentCountryPhaseId() {
        return parentCountryPhaseId;
    }

    public void setParentCountryPhaseId(BigInteger parentCountryPhaseId) {
        this.parentCountryPhaseId = parentCountryPhaseId;
    }

    public PhaseType getPhaseType() {
        return phaseType;
    }

    public void setPhaseType(PhaseType phaseType) {
        this.phaseType = phaseType;
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



    public int getDurationInDays() {
        return durationInDays;
    }

    public void setDurationInDays(int durationInDays) {
        this.durationInDays = durationInDays;
    }
}
