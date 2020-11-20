package com.kairos.dto.activity.unit_settings;

import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OpenShiftPhase {
    private BigInteger phaseId;
    private String phaseName;
    private boolean solveUnderStaffingOverStaffing;
    private int sequence;
    private Map<String, TranslationInfo> translations;

    public OpenShiftPhase(BigInteger phaseId,String phaseName,boolean solveUnderStaffingOverStaffing,int sequence){
      this.phaseId =phaseId;
      this.phaseName =phaseName;
      this.solveUnderStaffingOverStaffing =solveUnderStaffingOverStaffing;
      this.sequence =sequence;
    }

    public String getPhaseName(){
        return TranslationUtil.getName(TranslationUtil.convertUnmodifiableMapToModifiableMap(translations),phaseName);
    }
}


