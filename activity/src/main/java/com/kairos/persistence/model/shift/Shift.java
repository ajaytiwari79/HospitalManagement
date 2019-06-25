package com.kairos.persistence.model.shift;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.commons.audit_logging.IgnoreLogging;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.enums.shift.ShiftType;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;

/**
 * Created by vipul on 30/8/17.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "shifts")
public class Shift extends MongoBaseEntity {

    private Date startDate;
    private Date endDate;
    private boolean disabled = false;
    private long bid;
    private long pId;
    private long bonusTimeBank = 0;
    private long amount;
    private long probability = 0;
    private long accumulatedTimeBankInMinutes = 0;
    private String remarks;
    @NotNull(message = "error.ShiftDTO.staffId.notnull")
    private Long staffId;
    private BigInteger phaseId;
    private BigInteger planningPeriodId;
    private Integer weekCount;
    @Indexed

    private Long unitId;
    private int scheduledMinutes;
    private int durationMinutes;
    @NotEmpty(message = "message.shift.activity.empty")
    private List<ShiftActivity> activities;
    //time care id
    private String externalId;
    @NotNull(message = "error.ShiftDTO.employmentId.notnull")
    private Long employmentId;
    private BigInteger parentOpenShiftId;
    // from which shift it is copied , if we need to undo then we need this
    private BigInteger copiedFromShiftId;
    private boolean sickShift;
    private Long functionId;
    private Long staffUserId;
    private ShiftType shiftType;
    private int timeBankCtaBonusMinutes;
    private int plannedMinutesOfTimebank;
    private int payoutCtaBonusMinutes;
    private int plannedMinutesOfPayout;
    private int scheduledMinutesOfTimebank;
    private int scheduledMinutesOfPayout;
    private Shift draftShift;
    private boolean draft;
    private RequestAbsence requestAbsence;
    private List<ShiftActivity> breakActivities;


    public Shift() {
        //Default Constructor
    }


    public Shift(Date startDate, Date endDate, Long employmentId, @NotEmpty(message = "message.shift.activity.empty") List<ShiftActivity> shiftActivities) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.employmentId = employmentId;
        this.activities = shiftActivities;
    }

    public Shift(BigInteger id, Date startDate, Date endDate, long bid, long pId, long bonusTimeBank,
                 long amount, long probability, long accumulatedTimeBankInMinutes, String remarks,@NotEmpty(message = "message.shift.activity.empty") List<ShiftActivity> activities,@NotNull(message = "error.ShiftDTO.staffId.notnull") Long staffId, Long unitId, Long employmentId) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.bid = bid;
        this.pId = pId;
        this.bonusTimeBank = bonusTimeBank;
        this.amount = amount;
        this.probability = probability;
        this.accumulatedTimeBankInMinutes = accumulatedTimeBankInMinutes;
        this.remarks = remarks;
        this.activities = activities;
        this.staffId = staffId;
        this.unitId = unitId;
        this.employmentId = employmentId;

    }
    // This is used in absance shift
    public Shift(Date startDate, Date endDate, @NotNull(message = "error.ShiftDTO.staffId.notnull") Long staffId, @NotEmpty(message = "message.shift.activity.empty")List<ShiftActivity> activities, Long employmentId, Long unitId, BigInteger phaseId, BigInteger planningPeriodId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.staffId = staffId;
        this.activities = activities;
        this.employmentId = employmentId;
        this.unitId=unitId;
        this.sickShift=true;
        this.phaseId=phaseId;
        this.planningPeriodId=planningPeriodId;

    }

    public Shift(Date startDate, Date endDate, String remarks, @NotEmpty(message = "message.shift.activity.empty") List<ShiftActivity> activities, @NotNull(message = "error.ShiftDTO.staffId.notnull") Long staffId, Long unitId, int scheduledMinutes, int durationMinutes, String externalId, Long employmentId, BigInteger parentOpenShiftId, BigInteger copiedFromShiftId, BigInteger phaseId, BigInteger planningPeriodId, Long staffUserId, ShiftType shiftType) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.remarks = remarks;
        this.activities = activities;
        this.staffId = staffId;
        this.unitId = unitId;
        this.externalId = externalId;
        this.employmentId = employmentId;
        this.parentOpenShiftId = parentOpenShiftId;
        this.copiedFromShiftId = copiedFromShiftId;
        this.scheduledMinutes = scheduledMinutes;
        this.durationMinutes = durationMinutes;
        this.phaseId=phaseId;
        this.planningPeriodId=planningPeriodId;
        this.staffUserId=staffUserId;
        this.shiftType=shiftType;
    }

    public void setActivities(List<ShiftActivity> activities) {
        activities = isNull(activities) ? new ArrayList<>() : activities;
        Collections.sort(activities);
        this.activities = activities;
    }


    public int getMinutes() {
        DateTimeInterval interval = getInterval();
        return isNotNull(interval) ? (int)interval.getMinutes() : 0;
    }

    @IgnoreLogging
    public DateTimeInterval getInterval() {
        if(isCollectionNotEmpty(this.activities)) {
            return new DateTimeInterval(this.getActivities().get(0).getStartDate().getTime(), getActivities().get(getActivities().size() - 1).getEndDate().getTime());
        }
        return null;
    }

    @Override
    public String toString() {
        return "Shift{" +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", disabled=" + disabled +
                ", bid=" + bid +
                ", pId=" + pId +
                ", bonusTimeBank=" + bonusTimeBank +
                ", amount=" + amount +
                ", probability=" + probability +
                ", accumulatedTimeBankInMinutes=" + accumulatedTimeBankInMinutes +
                ", remarks='" + remarks + '\'' +
                ", staffId=" + staffId +
                ", weekCount=" + weekCount +
                ", unitId=" + unitId +
                '}';
    }
}
