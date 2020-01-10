package com.kairos.persistence.model.activity.tabs.rules_activity_tab;

import com.kairos.enums.sickness.ReplaceSickShift;
import com.kairos.enums.sickness.SickStaffNotApplicableFor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class SicknessSetting {


    private boolean canOnlyUsedOnMainEmployment;
    private boolean canNotUsedTopOfApprovedAbsences;
    private boolean validForChildCare;
    private boolean usedOnFreeDays;
    private boolean usedOnProtecedDaysOff;
    private Set<SickStaffNotApplicableFor> sickStaffNotApplicableFor;
    private boolean showAslayerOnTopOfPublishedShift;
    private boolean showAslayerOnTopOfUnPublishedShift;
    private ReplaceSickShift replaceSickShift;

}

