package com.kairos.persistence.model.activity.tabs.rules_activity_tab;

import com.kairos.dto.activity.activity.activity_tabs.CutOffInterval;
import com.kairos.dto.activity.activity.activity_tabs.CutOffIntervalUnit;
import com.kairos.dto.activity.activity.activity_tabs.PQLSettings;
import com.kairos.dto.activity.open_shift.DurationField;
import com.kairos.enums.reason_code.ReasonCodeRequiredState;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pawanmandhan on 23/8/17.
 */
@Getter
@Setter
public class RulesActivityTab{


    private boolean eligibleForFinalSchedule;
    private boolean eligibleForDraftSchedule;
    private boolean eligibleForRequest;
    private boolean lockLengthPresent;
    private boolean eligibleToBeForced;
    private List<Long> dayTypes= new ArrayList<>();
    private boolean eligibleForStaffingLevel;
    private boolean breakAllowed = false;
    private List<BigInteger> approvalAllowedPhaseIds=new ArrayList<>();
    private LocalDate cutOffStartFrom;
    private CutOffIntervalUnit cutOffIntervalUnit;
    private Integer cutOffdayValue;
    private List<CutOffInterval> cutOffIntervals;
    private CutOffIntervalUnit.CutOffBalances cutOffBalances;
    private boolean borrowLeave;
    private boolean transferAll;
    private int noOfTransferLeave;
    // in Minutes

    private LocalTime earliestStartTime;
    private LocalTime latestStartTime;
    private Short shortestTime;
    private Short longestTime;
    private boolean eligibleForCopy;
    private DurationField plannedTimeInAdvance;
    private LocalTime maximumEndTime;// shift can't be extend this time
    //remove after integration
    private boolean allowedAutoAbsence;
    private byte recurrenceDays;// if a staff fall sick and select this activity then for recurrence days and times --   -128 to 127
    private byte recurrenceTimes;// -- the  shift of that staff will be entered.     -128 to 127
    private PQLSettings pqlSettings=new PQLSettings();
    private ReasonCodeRequiredState reasonCodeRequiredState;
    private List<BigInteger> staffTagIds;
    private SicknessSetting sicknessSetting;
    private boolean sicknessSettingValid;

}
