package com.kairos.dto.activity.shift;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.enums.TimeTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;


/*
 * Created By Pradeep singh rajawat
 *  Date-27/01/2018`
 *
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class ShiftWithActivityDTO extends ShiftDTO{
    private PhaseDTO phase;
    private String timeType;
    @JsonIgnore
    private List<BigInteger> activitiesTimeTypeIds = new ArrayList<>();
    @JsonIgnore
    private List<BigInteger> activityIds = new ArrayList<>();
    @JsonIgnore
    private List<BigInteger> activitiesPlannedTimeIds = new ArrayList<>();
    private List<WorkTimeAgreementRuleViolation> wtaRuleViolations;

    public ShiftWithActivityDTO() {
    }

    public ShiftWithActivityDTO(Date startDate, Date endDate,List<ShiftActivityDTO> activities) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.activities = activities;
    }

    public List<BigInteger> getActivitiesTimeTypeIds(){
        if(activitiesTimeTypeIds.isEmpty()) {
            activitiesTimeTypeIds = activities.stream().filter(shiftActivityDTO -> shiftActivityDTO.getActivity()!=null).map(shiftActivityDTO -> shiftActivityDTO.getActivity().getBalanceSettingsActivityTab().getTimeTypeId()).collect(Collectors.toList());
        }
        return activitiesTimeTypeIds;
    }

    public List<BigInteger> getActivitiesPlannedTimeIds(){
        if(activitiesPlannedTimeIds.isEmpty()) {
            activitiesPlannedTimeIds = activities.stream().flatMap(k -> k.getPlannedTimes().stream().map(plannedTime->plannedTime.getPlannedTimeId())).collect(Collectors.toList());
        }
        return activitiesPlannedTimeIds;
    }

    @JsonIgnore
    public List<BigInteger> getActivityIds(){
        if(activityIds.isEmpty()) {
            activityIds = activities.stream().map(shiftActivityDTO -> shiftActivityDTO.getActivityId()).collect(Collectors.toList());
        }
        return activityIds;
    }

    public void setActivities(List<ShiftActivityDTO> activities) {
        if(isNotNull(activities)){
            activities.sort(Comparator.comparing(ShiftActivityDTO::getStartDate));
            this.activities = activities;
            this.startDate = activities.get(0).getStartDate();
            this.endDate = activities.get(activities.size()-1).getEndDate();
        }else {
            activities = new ArrayList<>();
        }
    }

    public void setEmploymentId(Long employmentId) {
        this.employmentId = employmentId;
    }

    public int getMinutes() {
        return 0;
//        return ((int) (this.activities.get(activities.size() - 1).getEndDate().getTime() - this.activities.get(0).getStartDate().getTime()) / 60000);
    }

    public boolean isPresence(){
        return this.getActivities().stream().anyMatch(shiftActivityDTO -> isNotNull(shiftActivityDTO.getActivity()) && TimeTypeEnum.PRESENCE.equals(shiftActivityDTO.getActivity().getBalanceSettingsActivityTab().getTimeType()));
    }

    public boolean isAbsence(){
        return this.getActivities().stream().allMatch(shiftActivityDTO -> isNotNull(shiftActivityDTO.getActivity()) && TimeTypeEnum.ABSENCE.equals(shiftActivityDTO.getActivity().getBalanceSettingsActivityTab().getTimeType()));
    }

    @JsonIgnore
    public ShiftActivityDTO getFirstActivity(){
        return this.getActivities().get(0);
    }

    @JsonIgnore
    public ShiftActivityDTO getLastActivity(){
        return this.getActivities().get(this.getActivities().size()-1);
    }

    @JsonIgnore
    public DateTimeInterval getDateTimeInterval() {
        return new DateTimeInterval(startDate.getTime(), endDate.getTime());
    }

    @JsonIgnore
    public LocalDate getEndLocalDate(){
        return asLocalDate(this.endDate);
    }

    public void resetTimebankDetails(){
        this.timeBankCtaBonusMinutes = 0;
        this.plannedMinutesOfTimebank = 0;
        this.getActivities().forEach(shiftActivityDTO -> shiftActivityDTO.resetTimebankDetails());
    }

}
