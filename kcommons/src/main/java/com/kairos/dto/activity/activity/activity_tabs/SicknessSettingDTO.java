package com.kairos.dto.activity.activity.activity_tabs;

import com.kairos.enums.sickness.ReplaceSickShift;
import com.kairos.enums.sickness.SickStaffNotApplicableFor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class SicknessSettingDTO {
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
