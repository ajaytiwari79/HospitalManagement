package com.kairos.persistence.model.activity.tabs.rules_activity_tab;

import com.kairos.enums.sickness.ReplaceSickctivityEnum;
import com.kairos.enums.sickness.StaffNotApplicableForEnum;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class SicknessSetting {
    private boolean layerForPublishedShift;
    private boolean layerForUnPublishedShift;
    private ReplaceSickctivityEnum replaceSkillActivityEnum;
    private boolean usedOnMainEmployment;
    private boolean allowedAutoAbsence;
    private byte recurrenceDays;// if a staff fall sick and select this activity then for recurrence days and times --   -128 to 127
    private byte recurrenceTimes;// -- the  shift of that staff will be entered.     -128 to 127
    private boolean validForChildCare;
    private List<BigInteger> staffTagIds;
    private boolean topOnApprovedAbsences;
    private boolean usedOnFreeDays;
    private boolean usedOnProtecedDaysOff;
    private Set<StaffNotApplicableForEnum> staffNotApplicableFor;

}

