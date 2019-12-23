package com.kairos.dto.activity.activity.activity_tabs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.activity.open_shift.DurationField;
import com.kairos.enums.reason_code.ReasonCodeRequiredState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by pawanmandhan on 23/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class RulesActivityTabDTO {

    private BigInteger activityId;
    private boolean eligibleForFinalSchedule;
    private boolean eligibleForDraftSchedule;
    private boolean eligibleForRequest;
    private boolean lockLengthPresent;
    private boolean eligibleToBeForced;
    private List<Long> dayTypes;
    private boolean eligibleForStaffingLevel;
    private boolean breakAllowed = false;
    private boolean approvalAllowed = false;
    private List<BigInteger> approvalAllowedPhaseIds;
    private LocalDate cutOffStartFrom;
    private CutOffIntervalUnit cutOffIntervalUnit;
    private Integer cutOffdayValue;
    private List<CutOffInterval> cutOffIntervals = new ArrayList<>();
    private CutOffIntervalUnit.CutOffBalances cutOffBalances;
    // in Minutes
    private LocalTime earliestStartTime;
    private LocalTime latestStartTime;
    private Short shortestTime;
    private Short longestTime;
    private boolean eligibleForCopy;
    private DurationField plannedTimeInAdvance;
    private LocalTime maximumEndTime;
    private boolean allowedAutoAbsence;
    private byte recurrenceDays;// if a staff fall sick and select this activity then for recurrence days and times --
    private byte recurrenceTimes;// -- the  shift of that staff will be entered.
    private PQLSettings pqlSettings;
    private boolean reasonCodeRequired;
    private ReasonCodeRequiredState reasonCodeRequiredState;
    private boolean borrowLeave;
    private boolean transferAll;
    private int noOfTransferLeave;
    private SicknessSettingDTO sicknessSetting;
    public PQLSettings getPqlSettings() {
        return pqlSettings=Optional.ofNullable(pqlSettings).orElse(new PQLSettings());
    }

}
