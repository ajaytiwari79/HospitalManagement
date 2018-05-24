package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.persistence.model.wta.templates.template_types.SeniorDaysPerYearWTATemplate;
import com.kairos.activity.util.DateTimeInterval;

import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.getIntervalByNumberOfWeeks;

/**
 * @author pradeep
 * @date - 22/5/18
 */

public class SeniorDaysPerYearWrapper implements RuleTemplateWrapper{


    private SeniorDaysPerYearWTATemplate wtaTemplate;
    private RuleTemplateSpecificInfo infoWrapper;

    public SeniorDaysPerYearWrapper(SeniorDaysPerYearWTATemplate ruleTemplate, RuleTemplateSpecificInfo ruleTemplateSpecificInfo) {
        this.wtaTemplate = ruleTemplate;
        this.infoWrapper = ruleTemplateSpecificInfo;

    }

    @Override
    public String isSatisfied() {
        if(wtaTemplate.getActivityIds().contains(infoWrapper.getShift().getActivity().getId())) {
            DateTimeInterval dateTimeInterval = getIntervalByNumberOfWeeks(infoWrapper.getShift(), wtaTemplate.getNumberOfWeeks().intValue(), wtaTemplate.getValidationStartDate());
        }
        return "";
    }
}
