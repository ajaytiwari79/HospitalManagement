package com.kairos.dto.activity.phase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.DurationType;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.phase.PhaseType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

/**
 * Created by vipul on 19/9/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
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
    private DayOfWeek untilNextDay;
    private int realtimeDuration;
    private String shortName;
    public Long getOrganizationId() {
        return organizationId;
    }
    private Set<Long> accessGroupIds;

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


    public PhaseWeeklyDTO buildWeekDTO() {
        PhaseWeeklyDTO phaseWeeklyDTO = new PhaseWeeklyDTO(id, name, description, duration, sequence, organizationId);
        return phaseWeeklyDTO;
    }


}
