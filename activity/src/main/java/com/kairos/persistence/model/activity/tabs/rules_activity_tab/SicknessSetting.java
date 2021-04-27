package com.kairos.persistence.model.activity.tabs.rules_activity_tab;

import com.kairos.enums.sickness.ReplaceSickShift;
import com.kairos.enums.sickness.SickStaffNotApplicableFor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
//Todo Team A
public class SicknessSetting implements Serializable {


    private static final long serialVersionUID = -2454050699794513683L;
    private boolean canOnlyUsedOnMainEmployment;
    private boolean canNotUsedTopOfApprovedAbsences;
    private boolean validForChildCare;
    private boolean usedOnFreeDays;
    private boolean usedOnProtecedDaysOff;
    private Set<SickStaffNotApplicableFor> sickStaffNotApplicableFor = new HashSet<>();
    private boolean showAslayerOnTopOfPublishedShift;
    private boolean showAslayerOnTopOfUnPublishedShift;
    private ReplaceSickShift replaceSickShift;

}

