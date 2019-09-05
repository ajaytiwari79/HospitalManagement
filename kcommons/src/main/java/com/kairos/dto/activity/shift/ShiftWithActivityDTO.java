package com.kairos.dto.activity.shift;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.shift.ShiftStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;


/*
 * Created By Pradeep singh rajawat
 *  Date-27/01/2018
 *
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
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
            activities.sort((a1,a2)->a1.getStartDate().compareTo(a2.getStartDate()));
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
        return ((int) (this.activities.get(activities.size() - 1).getEndDate().getTime() - this.activities.get(0).getStartDate().getTime()) / 60000);
    }

    public boolean isPresence(){
        return this.getActivities().stream().anyMatch(shiftActivityDTO -> isNotNull(shiftActivityDTO.getActivity()) && TimeTypeEnum.PRESENCE.equals(shiftActivityDTO.getActivity().getBalanceSettingsActivityTab().getTimeType()));
    }

    public boolean isAbsence(){
        return this.getActivities().stream().allMatch(shiftActivityDTO -> isNotNull(shiftActivityDTO.getActivity()) && TimeTypeEnum.ABSENCE.equals(shiftActivityDTO.getActivity().getBalanceSettingsActivityTab().getTimeType()));
    }

    @JsonIgnore
    public DateTimeInterval getDateTimeInterval() {
        return new DateTimeInterval(startDate.getTime(), endDate.getTime());
    }

    @JsonIgnore
    public LocalDate getEndLocalDate(){
        return asLocalDate(this.endDate);
    }

}
