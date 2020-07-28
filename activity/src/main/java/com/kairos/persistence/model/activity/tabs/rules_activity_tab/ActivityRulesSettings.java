package com.kairos.persistence.model.activity.tabs.rules_activity_tab;

import com.kairos.annotations.KPermissionField;
import com.kairos.annotations.KPermissionSubModel;
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
public class 
ActivityRulesSettings {


    private boolean eligibleForFinalSchedule;
    private boolean eligibleForDraftSchedule;
    private boolean eligibleForRequest;
    private boolean lockLengthPresent;
    private boolean eligibleToBeForced;
    @KPermissionField
    private List<Long> dayTypes= new ArrayList<>();
    @KPermissionField
    private boolean eligibleForStaffingLevel;
    @KPermissionField
    private boolean breakAllowed;
    @KPermissionField
    private List<BigInteger> approvalAllowedPhaseIds=new ArrayList<>();
    @KPermissionField
    private LocalDate cutOffStartFrom;
    @KPermissionField
    private CutOffIntervalUnit cutOffIntervalUnit;
    @KPermissionField
    private Integer cutOffdayValue;
    @KPermissionField
    private List<CutOffInterval> cutOffIntervals;
    @KPermissionField
    private CutOffIntervalUnit.CutOffBalances cutOffBalances;
    @KPermissionField
    private boolean borrowLeave;
    @KPermissionField
    private boolean transferAll;
    @KPermissionField
    private int noOfTransferLeave;
    // in Minutes
    @KPermissionField
    private LocalTime earliestStartTime;
    @KPermissionField
    private LocalTime latestStartTime;
    @KPermissionField
    private Short shortestTime;
    @KPermissionField
    private Short longestTime;
    private boolean eligibleForCopy;
    @KPermissionField
    private LocalTime maximumEndTime;// shift can't be extend this time
    //remove after integration
    @KPermissionField
    private boolean allowedAutoAbsence;
    private byte recurrenceDays;// if a staff fall sick and select this activity then for recurrence days and times --   -128 to 127
    private byte recurrenceTimes;// -- the  shift of that staff will be entered.     -128 to 127
    private PQLSettings pqlSettings=new PQLSettings();
    @KPermissionField
    private ReasonCodeRequiredState reasonCodeRequiredState;
    private List<BigInteger> staffTagIds;
    private SicknessSetting sicknessSetting=new SicknessSetting();
    private boolean sicknessSettingValid;

}
