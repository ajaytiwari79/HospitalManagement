package com.kairos.dto.activity.shift;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.user.country.experties.ExpertiseLineDTO;
import com.kairos.enums.shift.BreakPaymentSetting;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isNullOrElse;

/**
 * Created by vipul on 6/2/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Expertise {
    private Long id;
    private String name;
    private BreakPaymentSetting breakPaymentSetting;
    private List<ProtectedDaysOffSetting> protectedDaysOffSettings;
    private List<ExpertiseLineDTO> expertiseLines;

    public Expertise() {
        //Not in use
    }


    public List<ProtectedDaysOffSetting> getProtectedDaysOffSettings() {
        return isNullOrElse(protectedDaysOffSettings,new ArrayList<>());
    }

}
