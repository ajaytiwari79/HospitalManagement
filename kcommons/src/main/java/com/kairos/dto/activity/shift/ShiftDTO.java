package com.kairos.dto.activity.shift;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.common.UserInfo;
import com.kairos.dto.activity.pay_out.PayOutPerShiftCTADistributionDTO;
import com.kairos.dto.activity.time_bank.TimeBankDistributionDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.enums.shift.ShiftDeletedBy;
import com.kairos.enums.shift.ShiftEscalationReason;
import com.kairos.enums.shift.ShiftType;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.DateUtils.roundDateByMinutes;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.CommonConstants.MULTIPLE_ACTIVITY;

/**
 * Created by vipul on 30/8/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class ShiftDTO implements Comparable<ShiftDTO>{

    protected BigInteger id;
    protected Date startDate;
    protected Date endDate;
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
    protected boolean editable=true;
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
    private boolean sickShift;
    protected UserInfo createdBy;
    private boolean disabled;
    private ShiftViolatedRules shiftViolatedRules;
    private Map<String,Object> changes;
    protected List<TimeBankDistributionDTO> timeBankCTADistributions = new ArrayList<>();
    protected List<PayOutPerShiftCTADistributionDTO> payoutPerShiftCTADistributions = new ArrayList<>();
    private ShiftActivityDTO replacedActivity;
    private Date coverShiftDate;
    private boolean fillGap;
    private boolean createdByCoverShift;
    protected ShiftDeletedBy deletedBy;

    public ShiftDTO(Date startDate, Date endDate, @NotNull(message = "error.ShiftDTO.staffId.notnull") Long staffId, @NotEmpty(message = "message.shift.activity.empty") List<ShiftActivityDTO> activities, Long employmentId, Long unitId, BigInteger phaseId, BigInteger planningPeriodId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.staffId = staffId;
        this.activities = activities;
        this.employmentId = employmentId;
        this.unitId = unitId;
        this.sickShift = true;
        this.phaseId = phaseId;
        this.planningPeriodId = planningPeriodId;

    }

    public ShiftDTO(@NotNull(message = "message.shift.shiftDate") LocalDate shiftDate,List<ShiftActivityDTO> activities,BigInteger id) {
        this.shiftDate = shiftDate;
        this.activities = activities;
        this.id = id;
    }

    public ShiftDTO(Date startDate, Date endDate, List<ShiftActivityDTO> activities) {
        this.startDate = isNull(startDate) ? null : roundDateByMinutes(startDate,15);
        this.endDate = isNull(endDate) ? null : roundDateByMinutes(endDate,15);
        this.activities = activities;
    }

    public ShiftDTO(BigInteger id, Date startDate, Date endDate, Long unitId, Long staffId) {
       this.id = id;
       this.startDate = isNull(startDate) ? null : roundDateByMinutes(startDate,15);
       this.endDate = isNull(endDate) ? null : roundDateByMinutes(endDate,15);
       this.unitId = unitId;
       this.staffId = staffId;
   }

    public ShiftDTO(List<ShiftActivityDTO> activities,Long unitId, @Range(min = 0) @NotNull(message = "error.ShiftDTO.staffId.notnull") Long staffId, @Range(min = 0) @NotNull(message = "error.ShiftDTO.employmentId.notnull") Long employmentId) {
        this.activities = activities;
        this.unitId = unitId;
        this.staffId = staffId;
        this.employmentId = employmentId;
    }

    //Todo this constructor is only for absenceType of Activity
    public ShiftDTO(List<ShiftActivityDTO> activities, Long unitId, @Range(min = 0) @NotNull(message = "error.ShiftDTO.staffId.notnull") Long staffId, @Range(min = 0) @NotNull(message = "error.ShiftDTO.employmentId.notnull") Long employmentId, Date startDate, Date endDate) {
        this.activities = activities;
        this.unitId = unitId;
        this.staffId = staffId;
        this.employmentId = employmentId;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    public List<TimeBankDistributionDTO> getTimeBankCTADistributions() {
        this.timeBankCTADistributions = Optional.ofNullable(this.timeBankCTADistributions).orElse(new ArrayList<>());
        return this.timeBankCTADistributions;
    }

    public List<PayOutPerShiftCTADistributionDTO> getPayoutPerShiftCTADistributions() {
        this.payoutPerShiftCTADistributions = Optional.ofNullable(this.payoutPerShiftCTADistributions).orElse(new ArrayList<>());
        return this.payoutPerShiftCTADistributions;
    }

    @JsonIgnore
    public DateTimeInterval getInterval() {
        return new DateTimeInterval(this.getActivities().get(0).getStartDate().getTime(), this.getActivities().get(this.getActivities().size()-1).getEndDate().getTime());
    }

    public void setActivities(List<ShiftActivityDTO> activities) {
        if (Optional.ofNullable(activities).isPresent() && activities.size()>1) {
            activities = activities.stream().filter(shiftActivityDTO -> Optional.ofNullable(shiftActivityDTO.getStartDate()).isPresent()).sorted(Comparator.comparing(ShiftActivityDTO::getStartDate)).collect(Collectors.toList());
            activities = ObjectUtils.mergeShiftActivity(activities);
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

    @JsonIgnore
    public void mergeShiftActivity(){
        if(isCollectionNotEmpty(activities)) {
            Collections.sort(activities);
            ShiftActivityDTO activityDTO = activities.get(0);
            List<ShiftActivityDTO> mergedShiftActivityDTOS = new ArrayList<>();
            for (ShiftActivityDTO shiftActivityDTO : activities) {
                if (activityDTO.getEndDate().equals(shiftActivityDTO.getStartDate()) && activityDTO.getActivityId().equals(shiftActivityDTO.getActivityId())) {
                    activityDTO.setEndDate(shiftActivityDTO.getEndDate());
                } else if ((activityDTO.getEndDate().before(shiftActivityDTO.getStartDate())) || activityDTO.getEndDate().equals(shiftActivityDTO.getStartDate()) && !activityDTO.getActivityId().equals(shiftActivityDTO.getActivityId())) {
                    mergedShiftActivityDTOS.add(activityDTO);
                    activityDTO = shiftActivityDTO;
                }
            }
            //to add last one
            mergedShiftActivityDTOS.add(activityDTO);
            activities = mergedShiftActivityDTOS;
        }
    }

    //todo don't remove this method it is for frontend
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


    public void setStartDate(Date startDate) {
        this.startDate = isNull(startDate) ? null : roundDateByMinutes(startDate,15);
    }

    public void setEndDate(Date endDate) {
        this.endDate = isNull(endDate) ? null : roundDateByMinutes(endDate,15);;
    }

    public void setStartDateAndEndDate(Date startDate,Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @JsonIgnore
    public boolean isOverNightShift(){
        return !asLocalDate(this.startDate).equals(asLocalDate(this.endDate));
    }

    public LocalDate getShiftDate() {
        return shiftDate=shiftDate==null?this.getActivities().get(0).getStartLocalDate():shiftDate;
    }

    @Override
    public String toString() {
        return "ShiftDTO{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", remarks='" + remarks + '\'' +
                ", unitId=" + unitId +
                ", staffId=" + staffId +
                '}';
    }

    @Override
    public int compareTo(ShiftDTO shiftDTO) {
        return this.startDate.compareTo(shiftDTO.startDate);
    }
}
