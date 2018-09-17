package com.kairos.dto.activity.phase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.phase.PhaseType;
import com.kairos.enums.DurationType;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalTime;
import java.util.List;

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
    private List<String> status;
    private String color;
    private PhaseDefaultName phaseEnum;
    private LocalTime flippingDefaultTime;
    private int gracePeriodByStaff;
    private int gracePeriodByManagement;
    private String untilNextDay;
    private int realtimeDuration;
    private String shortName;
    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public PhaseDTO() {
        //default cons
    }

    public PhaseDTO(@NotNull(message = "error.phase.name.notnull") String name, String description, PhaseDefaultName phaseEnum, @Range(min = 0) int duration, DurationType durationType, int sequence, Long countryId) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.durationType = durationType;
        this.sequence = sequence;
        this.countryId = countryId;
        this.phaseEnum=phaseEnum;
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

    public List<String> getStatus() {
        return status;
    }

    public void setStatus(List<String> status) {
        this.status = status;
    }


    public PhaseWeeklyDTO buildWeekDTO() {
        PhaseWeeklyDTO phaseWeeklyDTO = new PhaseWeeklyDTO(id, name, description, duration, sequence, organizationId);
        return phaseWeeklyDTO;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getDurationInDays() {
        return durationInDays;
    }

    public void setDurationInDays(int durationInDays) {
        this.durationInDays = durationInDays;
    }

    public PhaseDefaultName getPhaseEnum() {
        return phaseEnum;
    }

    public void setPhaseEnum(PhaseDefaultName phaseEnum) {
        this.phaseEnum = phaseEnum;
    }

    public LocalTime getFlippingDefaultTime() {
        return flippingDefaultTime;
    }

    public void setFlippingDefaultTime(LocalTime flippingDefaultTime) {
        this.flippingDefaultTime = flippingDefaultTime;
    }

    public int getGracePeriodByStaff() {
        return gracePeriodByStaff;
    }

    public void setGracePeriodByStaff(int gracePeriodByStaff) {
        this.gracePeriodByStaff = gracePeriodByStaff;
    }

    public int getGracePeriodByManagement() {
        return gracePeriodByManagement;
    }

    public void setGracePeriodByManagement(int gracePeriodByManagement) {
        this.gracePeriodByManagement = gracePeriodByManagement;
    }

    public String getUntilNextDay() {
        return untilNextDay;
    }

    public void setUntilNextDay(String untilNextDay) {
        this.untilNextDay = untilNextDay;
    }

    public int getRealtimeDuration() {
        return realtimeDuration;
    }

    public void setRealtimeDuration(int realtimeDuration) {
        this.realtimeDuration = realtimeDuration;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
