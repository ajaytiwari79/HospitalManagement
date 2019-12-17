package com.kairos.dto.activity.period;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.DurationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by prerna on 10/4/18.
 */
@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanningPeriodDTO {
    private BigInteger id;
    private String name;
    private String dateRange;
    @NotNull(message = "error.startdate.notnull")
    private LocalDate startDate;
    private LocalDate endDate;
    private Long unitId = -1L;
   @Positive(message = "message.valid.duration")
    private int duration;
    private DurationType durationType;
    private int recurringNumber; // TODO HARISH rename
    private String currentPhase;
    private BigInteger currentPhaseId;
    private String nextPhase;
    private FlippingDateDTO requestToPuzzleDate;
    private FlippingDateDTO puzzleToConstructionDate;
    private FlippingDateDTO constructionToDraftDate;
    private List<PeriodPhaseDTO> phaseFlippingDate;
    private String periodDuration;
    private boolean active=true;
    private Set<Long> publishEmploymentIds=new HashSet<>();
    private String color;

    public PlanningPeriodDTO( LocalDate startDate, int duration, DurationType durationType, int recurringNumber, LocalDate endDate){
        this.startDate = startDate;
        this.duration = duration;
        this.durationType = durationType;
        this.recurringNumber = recurringNumber;
        this.endDate = endDate;
    }


}

