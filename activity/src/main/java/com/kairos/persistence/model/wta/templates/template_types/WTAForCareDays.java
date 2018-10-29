package com.kairos.persistence.model.wta.templates.template_types;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.shift.WorkTimeAgreementRuleViolation;
import com.kairos.dto.activity.wta.templates.ActivityCareDayCount;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.kairos.utils.ShiftValidatorService.*;

/**
 * @author pradeep
 * @date - 10/10/18
 */

public class WTAForCareDays extends WTABaseRuleTemplate{

    private List<ActivityCareDayCount> careDayCounts = new ArrayList<>();

    public WTAForCareDays(String name, String description) {
        super(name, description);
    }

    public WTAForCareDays() {
        wtaTemplateType = WTATemplateType.WTA_FOR_CARE_DAYS;
    }

    public List<ActivityCareDayCount> getCareDayCounts() {
        return careDayCounts;
    }

    public void setCareDayCounts(List<ActivityCareDayCount> careDayCounts) {
        this.careDayCounts = careDayCounts;
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        if(!isDisabled() && isValidForPhase(infoWrapper.getPhase(),this.phaseTemplateValues)) {
            for (ActivityCareDayCount careDayCount : careDayCounts) {
                Activity activity = infoWrapper.getActivityWrapperMap().get(careDayCount.getActivityId()).getActivity();
                List<ShiftWithActivityDTO> shifts = getShiftsByIntervalAndActivityIds(activity,Arrays.asList(infoWrapper.getShift(),infoWrapper.getShift()),Arrays.asList(careDayCount.getActivityId()));
                Integer[] limitAndCounter = getValueByPhase(infoWrapper, phaseTemplateValues, this);
                if(limitAndCounter[0] < shifts.size()){
                    WorkTimeAgreementRuleViolation workTimeAgreementRuleViolation = new WorkTimeAgreementRuleViolation(this.id,this.name,0,true,false);
                    infoWrapper.getViolatedRules().getWorkTimeAgreements().add(workTimeAgreementRuleViolation);
                    break;
                }
            }
        }
    }



}
