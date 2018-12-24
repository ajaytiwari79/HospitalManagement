package com.kairos.dto.activity.shift;

import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.time_bank.TimeBankCTADistributionDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.enums.shift.ShiftStatus;

import java.math.BigInteger;
import java.util.*;

/**
 * @author pradeep
 * @date - 13/9/18
 */

public class ShiftActivityDTO {

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
    private long bid;
    private long pId;
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
    private BigInteger plannedTimeId;
    private boolean breakShift;
    private boolean breakReplaced;
    private ReasonCodeDTO reasonCode;
    private Long allowedBreakDurationInMinute;

    private int timeBankCtaBonusMinutes;
    private List<TimeBankCTADistributionDTO> timeBankCTADistributions = new ArrayList<>();
    private Map<String, Object> location;// location where this activity needs to perform
    private String description;// this is from activity description and used in shift detail popup
    private List<WorkTimeAgreementRuleViolation> wtaRuleViolations;

    public ShiftActivityDTO(String activityName, Date startDate, Date endDate,BigInteger activityId) {
        this.activityId = activityId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.activityName = activityName;
    }

    public ShiftActivityDTO(String activityName, BigInteger activityId, String message, boolean success) {
        this.message = message;
        this.success = success;
        this.activityId = activityId;
        this.activityName = activityName;
    }

    public ShiftActivityDTO() {
    }

    public ShiftActivityDTO(BigInteger activityId, String activityName) {
        this.activityId = activityId;
        this.activityName = activityName;
    }

    public Long getAllowedBreakDurationInMinute() {
        return allowedBreakDurationInMinute;
    }

    public void setAllowedBreakDurationInMinute(Long allowedBreakDurationInMinute) {
        this.allowedBreakDurationInMinute = allowedBreakDurationInMinute;
    }

    public BigInteger getPlannedTimeId() {
        return plannedTimeId;
    }

    public void setPlannedTimeId(BigInteger plannedTimeId) {
        this.plannedTimeId = plannedTimeId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Set<ShiftStatus> getStatus() {
        return status;
    }

    public void setStatus(Set<ShiftStatus> status) {
        this.status = status;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getTimeType() {
        return timeType;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }

    public ActivityDTO getActivity() {
        return activity;
    }

    public void setActivity(ActivityDTO activity) {
        this.activity = activity;
    }

    public long getBid() {
        return bid;
    }

    public void setBid(long bid) {
        this.bid = bid;
    }

    public long getpId() {
        return pId;
    }

    public void setpId(long pId) {
        this.pId = pId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getScheduledMinutes() {
        return scheduledMinutes;
    }

    public void setScheduledMinutes(int scheduledMinutes) {
        this.scheduledMinutes = scheduledMinutes;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Long getAbsenceReasonCodeId() {
        return absenceReasonCodeId;
    }

    public void setAbsenceReasonCodeId(Long absenceReasonCodeId) {
        this.absenceReasonCodeId = absenceReasonCodeId;
    }

    public ReasonCodeDTO getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(ReasonCodeDTO reasonCode) {
        this.reasonCode = reasonCode;
    }

    public Long getReasonCodeId() {
        return reasonCodeId;
    }

    public void setReasonCodeId(Long reasonCodeId) {
        this.reasonCodeId = reasonCodeId;
    }

    public boolean isHaltBreak() {
        return haltBreak;
    }

    public void setHaltBreak(boolean haltBreak) {
        this.haltBreak = haltBreak;
    }

    public boolean isBreakShift() {
        return breakShift;
    }

    public void setBreakShift(boolean breakShift) {
        this.breakShift = breakShift;
    }

    public boolean isBreakReplaced() {
        return breakReplaced;
    }

    public void setBreakReplaced(boolean breakReplaced) {
        this.breakReplaced = breakReplaced;
    }

    public Map<String, Object> getLocation() {
        return location;
    }

    public void setLocation(Map<String, Object> location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<WorkTimeAgreementRuleViolation> getWtaRuleViolations() {
        return wtaRuleViolations;
    }

    public void setWtaRuleViolations(List<WorkTimeAgreementRuleViolation> wtaRuleViolations) {
        this.wtaRuleViolations = wtaRuleViolations;
    }
    public int getTimeBankCtaBonusMinutes() {
        return timeBankCtaBonusMinutes;
    }

    public void setTimeBankCtaBonusMinutes(int timeBankCtaBonusMinutes) {
        this.timeBankCtaBonusMinutes = timeBankCtaBonusMinutes;
    }

    public List<TimeBankCTADistributionDTO> getTimeBankCTADistributions() {
        return Optional.ofNullable( timeBankCTADistributions).orElse(new ArrayList<>(0));
    }

    public void setTimeBankCTADistributions(List<TimeBankCTADistributionDTO> timeBankCTADistributions) {
        this.timeBankCTADistributions = timeBankCTADistributions;
    }
}
