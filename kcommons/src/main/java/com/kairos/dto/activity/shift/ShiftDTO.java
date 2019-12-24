package com.kairos.dto.activity.shift;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.common.UserInfo;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.shift.ShiftEscalationReason;
import com.kairos.enums.shift.ShiftType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.joda.time.Duration;
import org.joda.time.Interval;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.CommonConstants.MULTIPLE_ACTIVITY;

/**
 * Created by vipul on 30/8/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ShiftDTO {

    protected BigInteger id;
    protected Date startDate;
    protected Date endDate;
    protected long bid;
    protected long pId;
    protected long amount;
    protected long probability;
    protected String remarks;
    protected BigInteger parentOpenShiftId;
    protected Long unitId;
    @Range(min = 0)
    @NotNull(message = "error.ShiftDTO.staffId.notnull")
    protected Long staffId;
    @Range(min = 0)
    @NotNull(message = "error.ShiftDTO.employmentId.notnull")
    protected Long employmentId;
    @NotNull(message = "message.shift.shiftDate")
    protected LocalDate shiftDate;
    protected Long allowedBreakDurationInMinute;
    protected ShiftTemplateDTO template;
    @NotEmpty(message = "message.shift.activity.empty")
    protected List<ShiftActivityDTO> activities = new ArrayList<>();
    protected int scheduledMinutes;
    protected int durationMinutes;
    protected BigInteger plannedTimeId;
    protected Long expertiseId;
    protected LocalDate validated;
    protected LocalDateTime clockIn;
    protected LocalDateTime clockOut;
    protected BigInteger shiftId;
    protected AccessGroupRole accessGroupRole;
    protected boolean editable;
    protected boolean functionDeleted;
    protected ShiftType shiftType;
    protected BigInteger shiftStatePhaseId;
    protected int timeBankCtaBonusMinutes;
    protected int payoutCtaBonusMinutes;
    protected int plannedMinutesOfTimebank;
    protected int plannedMinutesOfPayout;
    protected boolean multipleActivity;
    protected BigInteger planningPeriodId;
    protected BigInteger phaseId;
    protected int restingMinutes;
    protected Set<ShiftEscalationReason> escalationReasons;
    protected Long functionId;
    protected Set<BigInteger> escalationFreeShiftIds;
    protected boolean escalationResolved;
    protected boolean deleted;
    protected ShiftWithActivityDTO draftShift;
    protected boolean draft;
    protected RequestAbsenceDTO requestAbsence;
    protected List<ShiftActivityDTO> breakActivities;
    protected boolean hasOriginalShift;
    protected UserInfo createdBy;


    public ShiftDTO() {
        //default Const
    }

    public ShiftDTO(@NotNull(message = "message.shift.shiftDate") LocalDate shiftDate,List<ShiftActivityDTO> activities,BigInteger id) {
        this.shiftDate = shiftDate;
        this.activities = activities;
        this.id = id;
    }

    public ShiftDTO(Date startDate, Date endDate, List<ShiftActivityDTO> activities) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.activities = activities;
    }

    public ShiftDTO(BigInteger id, Date startDate, Date endDate, Long unitId, Long staffId) {
       this.id = id;
       this.startDate = startDate;
       this.endDate = endDate;
       this.unitId = unitId;
       this.staffId = staffId;
   }

    public ShiftDTO(List<ShiftActivityDTO> activities,Long unitId, @Range(min = 0) @NotNull(message = "error.ShiftDTO.staffId.notnull") Long staffId, @Range(min = 0) @NotNull(message = "error.ShiftDTO.employmentId.notnull") Long employmentId) {
        this.activities = activities;
        this.unitId = unitId;
        this.staffId = staffId;
        this.employmentId = employmentId;
    }

    public ShiftDTO(List<ShiftActivityDTO> activities, Long unitId, @Range(min = 0) @NotNull(message = "error.ShiftDTO.staffId.notnull") Long staffId, @Range(min = 0) @NotNull(message = "error.ShiftDTO.employmentId.notnull") Long employmentId, Date startDate, Date endDate) {
        this.activities = activities;
        this.unitId = unitId;
        this.staffId = staffId;
        this.employmentId = employmentId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @JsonIgnore
    public DateTimeInterval getInterval() {
        return new DateTimeInterval(this.getActivities().get(0).getStartDate().getTime(), this.getActivities().get(this.getActivities().size()-1).getEndDate().getTime());
    }

    public void setActivities(List<ShiftActivityDTO> activities) {
        if (Optional.ofNullable(activities).isPresent() && activities.size()>1) {
            activities = activities.stream().filter(shiftActivityDTO -> Optional.ofNullable(shiftActivityDTO.getStartDate()).isPresent()).sorted((s1, s2) -> s1.getStartDate().compareTo(s2.getStartDate())).collect(Collectors.toList());
        }
        this.activities = activities;
    }


    @JsonIgnore
    public Duration getDuration() {
        return new Interval(this.activities.get(0).getStartDate().getTime(), this.activities.get(activities.size()-1).getEndDate().getTime()).toDuration();
    }

    public void setBreakActivities(List<ShiftActivityDTO> breakActivities) {
        this.breakActivities = isNullOrElse(breakActivities,new ArrayList<>());
    }

    public List<ShiftActivityDTO> getBreakActivities() {
        this.breakActivities = isNullOrElse(this.breakActivities,new ArrayList<>());
        return breakActivities;
    }

    public boolean isMultipleActivity() {
        Set<BigInteger> multipleActivityCount = new HashSet<>();
        for (ShiftActivityDTO activity : this.getActivities()) {
            if (!activity.isBreakShift()) {
                multipleActivityCount.add(activity.getActivityId());
            }
        }
        return multipleActivityCount.size() > MULTIPLE_ACTIVITY;
    }

    public Date getStartDate() {
         if(isNull(startDate) && isCollectionNotEmpty(activities)){
            activities.sort(Comparator.comparing(ShiftActivityDTO::getStartDate));
            this.startDate = activities.get(0).getStartDate();
         }
         return this.startDate;
    }

    public Date getEndDate() {
        if(isNull(endDate) && isCollectionNotEmpty(activities)){
            activities.sort(Comparator.comparing(ShiftActivityDTO::getStartDate));
            this.endDate = activities.get(activities.size()-1).getEndDate();
        }
        return this.endDate;
    }

    public void setEscalationReasons(Set<ShiftEscalationReason> escalationReasons) {
        this.escalationReasons = isNullOrElse(escalationReasons,new HashSet<>());
    }
    public Set<BigInteger> getEscalationFreeShiftIds() {
        return escalationFreeShiftIds=Optional.ofNullable(escalationFreeShiftIds).orElse(new HashSet<>());
    }

    @Override
    public String toString() {
        return "ShiftDTO{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", bid=" + bid +
                ", pId=" + pId +
                ", amount=" + amount +
                ", probability=" + probability +
                ", remarks='" + remarks + '\'' +
                ", unitId=" + unitId +
                ", staffId=" + staffId +
                '}';
    }
}
