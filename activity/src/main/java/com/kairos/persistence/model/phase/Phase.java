package com.kairos.persistence.model.phase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.DurationType;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.phase.PhaseType;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

/**
 * Created by vipul on 25/9/17.
 */
@Document(collection = "phases")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Phase extends MongoBaseEntity {
    @NotNull(message = "error.phase.name.notnull")
    private String name;
    private String description;
    private int duration;
    private DurationType durationType;
    private int sequence;
    @Indexed
    private Long organizationId;
    private Long countryId;
    private BigInteger parentCountryPhaseId;
    private PhaseType phaseType;
    private List<ShiftStatus> status;
    private String color;
    private PhaseDefaultName phaseEnum;
    private LocalTime flippingDefaultTime;
    private int gracePeriodByStaff;// in days
    private int gracePeriodByManagement;// in days
    private DayOfWeek untilNextDay;
    private int realtimeDuration;
    private String shortName;
    private List<Long> accessGroupIds;

    public Phase(String name, String description, PhaseDefaultName phaseEnum, int duration, DurationType durationType, int sequence, Long countryId, Long organizationId, BigInteger parentCountryPhaseId, PhaseType phaseType, List<String> status, String color, LocalTime flippingDefaultTime) {
        this.name = name;
        this.description = description;
        this.phaseEnum = phaseEnum;
        this.duration = duration;
        this.durationType = durationType;
        this.sequence = sequence;
        this.countryId = countryId;
        this.organizationId = organizationId;
        this.parentCountryPhaseId = parentCountryPhaseId;
        this.phaseType = phaseType;
        this.status = ShiftStatus.getListByValue(status);
        this.color = color;
        this.flippingDefaultTime = flippingDefaultTime;
    }

    public Phase(String name, String description, PhaseDefaultName phaseEnum, int duration, DurationType durationType, int sequence, Long countryId, Long organizationId, BigInteger parentCountryPhaseId, PhaseType phaseType, List<String> status, String color, LocalTime flippingDefaultTime, int gracePeriodByStaff, int gracePeriodByManagement, DayOfWeek untilNextDay, int realtimeDuration) {
        this.name = name;
        this.description = description;
        this.phaseEnum = phaseEnum;
        this.duration = duration;
        this.durationType = durationType;
        this.sequence = sequence;
        this.countryId = countryId;
        this.organizationId = organizationId;
        this.parentCountryPhaseId = parentCountryPhaseId;
        this.phaseType = phaseType;
        this.status = ShiftStatus.getListByValue(status);
        this.color = color;
        this.flippingDefaultTime = flippingDefaultTime;
        this.gracePeriodByStaff = gracePeriodByStaff;
        this.gracePeriodByManagement = gracePeriodByManagement;
        this.untilNextDay = untilNextDay;
        this.realtimeDuration = realtimeDuration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Phase phase = (Phase) o;

        return new EqualsBuilder()
                .append(duration, phase.duration)
                .append(sequence, phase.sequence)
                .append(organizationId, phase.organizationId)
                .append(name, phase.name)
                .append(description, phase.description)
                .append(durationType, phase.durationType)
                .append(countryId, phase.countryId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(description)
                .append(duration)
                .append(durationType)
                .append(sequence)
                .append(organizationId)
                .append(countryId)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "Phase{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                ", durationType=" + durationType +
                ", sequence=" + sequence +
                ", organizationId=" + organizationId +
                ", countryId=" + countryId +
                '}';
    }

}
