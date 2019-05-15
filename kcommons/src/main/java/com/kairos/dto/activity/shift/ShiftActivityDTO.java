package com.kairos.dto.activity.shift;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kairos.commons.IgnoreLogging;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.time_bank.TimeBankDistributionDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.shift.ShiftStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;

/**
 * @author pradeep
 * @date - 13/9/18
 */
@Getter
@Setter
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
    private List<TimeBankDistributionDTO> timeBankCTADistributions = new ArrayList<>();
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

    public ShiftActivityDTO(String activityName, Date startDate, Date endDate,BigInteger activityId,Long absenceReasonCodeId) {
        this.activityId = activityId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.activityName = activityName;
        this.absenceReasonCodeId=absenceReasonCodeId;
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

    public ShiftActivityDTO(String activityName, Date startDate,Date endDate,BigInteger activityId,int scheduledMinutes,Set<ShiftStatus> status,ActivityDTO activity) {
        this.activityId = activityId;
        this.scheduledMinutes = scheduledMinutes;
        this.startDate = startDate;
        this.endDate = endDate;
        this.activityName = activityName;
        this.status=status;
        this.activity = activity;
    }
    public ShiftActivityDTO() {
    }

    public ShiftActivityDTO(BigInteger activityId, String activityName) {
        this.activityId = activityId;
        this.activityName = activityName;
    }

    public Set<ShiftStatus> getStatus() {
        return isNull(status) ? new HashSet<>() : status;
    }

    public void setStatus(Set<ShiftStatus> status) {
        this.status = isNull(status) ? new HashSet<>() : status;
    }

    public List<TimeBankDistributionDTO> getTimeBankCTADistributions() {
        return Optional.ofNullable( timeBankCTADistributions).orElse(new ArrayList<>(0));
    }

    @JsonIgnore
    @IgnoreLogging
    public LocalDate getStartLocalDate(){
        if(isNull(this.startDate)){
            return null;
        }
        return asLocalDate(this.startDate);
    }

    @JsonIgnore
    @IgnoreLogging
    public LocalDate getEndLocalDate(){
        if(isNull(this.endDate)){
            return null;
        }
        return asLocalDate(this.endDate);
    }


}
