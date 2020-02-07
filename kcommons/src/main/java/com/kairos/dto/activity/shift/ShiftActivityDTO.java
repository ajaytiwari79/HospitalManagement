package com.kairos.dto.activity.shift;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.pay_out.PayOutPerShiftCTADistributionDTO;
import com.kairos.dto.activity.time_bank.TimeBankDistributionDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.shift.ShiftType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.DateUtils.roundDateByMinutes;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.commons.utils.ObjectUtils.isNullOrElse;

/**
 * @author pradeep
 * @date - 13/9/18
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class ShiftActivityDTO implements Comparable<ShiftActivityDTO>{

    private Set<ShiftStatus> status;
    private String message;
    private boolean success;
    //This field is only for validation
    //@JsonIgnore
    private ActivityDTO activity;
    private BigInteger activityId;
    private Date startDate;
    private Date endDate;
    private int scheduledMinutes;
    private int durationMinutes;
    private String activityName;
    //used in T&A view
    private Long reasonCodeId;
    //used for adding absence type of activities.
    private Long absenceReasonCodeId;
    private String remarks;
    //please don't use this id for any functionality this only for frontend
    private BigInteger id;
    private String timeType;
    private String backgroundColor;
    private boolean haltBreak;
    private boolean breakShift;
    private boolean breakReplaced;
    private ReasonCodeDTO reasonCode;
    private Long allowedBreakDurationInMinute;

    private int timeBankCtaBonusMinutes;
    @Builder.Default
    private List<TimeBankDistributionDTO> timeBankCTADistributions = new ArrayList<>();
    private List<PayOutPerShiftCTADistributionDTO> payoutPerShiftCTADistributions;
    private Map<String, Object> location;// location where this activity needs to perform
    private String description;// this is from activity description and used in shift detail popup
    private List<WorkTimeAgreementRuleViolation> wtaRuleViolations;
    private int plannedMinutesOfTimebank;
    private String startLocation; // this is for the location from where activity will gets starts
    private String endLocation;   // this is for the location from where activity will gets ends
    private int scheduledMinutesOfTimebank;
    private int scheduledMinutesOfPayout;
    private TimeTypeEnum secondLevelType;
    private BigInteger secondLevelTimeTypeId;
    private BigInteger thirdLevelTimeTypeId;
    private BigInteger fourthLevelTimeTypeId;
    private List<PlannedTime> plannedTimes;
    private BigInteger plannedTimeId;
    private int plannedMinutesOfPayout;
    private int payoutCtaBonusMinutes;
    @Builder.Default
    private List<ShiftActivityDTO> childActivities = new ArrayList<>();
    private boolean breakNotHeld;
    private Long employmentId;
    private BigInteger phaseId;
    private ShiftType shiftType;
    private boolean breakInterrupt;

    public ShiftActivityDTO(Date startDate, Date endDate) {
        this.startDate = isNull(startDate) ? null : roundDateByMinutes(startDate,15);
        this.endDate = isNull(endDate) ? null : roundDateByMinutes(endDate,15);
    }

    public ShiftActivityDTO(String activityName, Date startDate, Date endDate, BigInteger activityId, Long absenceReasonCodeId) {
        this.activityId = activityId;
        this.startDate = isNull(startDate) ? null : roundDateByMinutes(startDate,15);
        this.endDate = isNull(endDate) ? null : roundDateByMinutes(endDate,15);
        this.activityName = activityName;
        this.absenceReasonCodeId=absenceReasonCodeId;
    }
    public ShiftActivityDTO(String activityName, Date startDate, Date endDate, BigInteger activityId, String message, boolean success){
        this.activityName=activityName;
        this.startDate=isNull(startDate) ? null : roundDateByMinutes(startDate,15);
        this.endDate=isNull(endDate) ? null : roundDateByMinutes(endDate,15);
        this.activityId=activityId;
        this.message=message;
        this.success=success;
    }

    public ShiftActivityDTO(String activityName, BigInteger id, String message, boolean success) {
        this.message = message;
        this.success = success;
        this.id = id;
        this.activityName = activityName;
    }
    public ShiftActivityDTO(String activityName, BigInteger id, String message, boolean success,Set<ShiftStatus> status) {
        this.message = message;
        this.success = success;
        this.id = id;
        this.activityName = activityName;
        this.status=status;
    }

    public ShiftActivityDTO() {
    }

    public ShiftActivityDTO(BigInteger activityId, String activityName,Set<ShiftStatus> status) {
        this.activityId = activityId;
        this.activityName = activityName;
        this.status = status;
    }

    public List<ShiftActivityDTO> getChildActivities() {
        return isNullOrElse(this.childActivities,new ArrayList<>());
    }

    @JsonIgnore
    public DateTimeInterval getInterval(){
        return new DateTimeInterval(this.startDate,this.endDate);
    }

    public Set<ShiftStatus> getStatus() {
        return isNull(status) ? new HashSet<>() : status;
    }

    public void setStatus(Set<ShiftStatus> status) {
        this.status = isNull(status) ? new HashSet<>() : status;
    }

    public List<TimeBankDistributionDTO> getTimeBankCTADistributions() {
        return Optional.ofNullable( timeBankCTADistributions).orElse(new ArrayList<>());
    }

    public List<PayOutPerShiftCTADistributionDTO> getPayoutPerShiftCTADistributions() {
        return Optional.ofNullable(payoutPerShiftCTADistributions).orElse(new ArrayList<>());
    }

    public List<PlannedTime> getPlannedTimes() {
        return Optional.ofNullable(plannedTimes).orElse(new ArrayList<>());
    }

    public void setPlannedTimes(List<PlannedTime> plannedTimes) {
        this.plannedTimes = plannedTimes;
    }

    @JsonIgnore
    public LocalDate getStartLocalDate(){
        return asLocalDate(this.startDate);
    }

    @JsonIgnore
    public LocalDate getEndLocalDate(){
        return asLocalDate(this.endDate);
    }

    @JsonIgnore
    public int getMinutes(){
        return (int)getInterval().getMinutes();
    }

    public int getTotalPlannedMinutes(){
        return plannedMinutesOfPayout + plannedMinutesOfTimebank;
    }

    public int getTotalCtaBonusMinutes(){
        return payoutCtaBonusMinutes + timeBankCtaBonusMinutes;
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

    public void resetTimebankDetails(){
        this.plannedMinutesOfTimebank = 0;
        this.timeBankCtaBonusMinutes = 0;
        this.timeBankCTADistributions = new ArrayList<>();
        this.getChildActivities().forEach(shiftActivityDTO -> shiftActivityDTO.resetTimebankDetails());
    }

    @Override
    public int compareTo(ShiftActivityDTO shiftActivityDTO) {
        return this.startDate.compareTo(shiftActivityDTO.startDate);
    }
}
