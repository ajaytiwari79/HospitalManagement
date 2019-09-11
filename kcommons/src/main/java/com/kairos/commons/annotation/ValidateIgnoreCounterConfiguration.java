package com.kairos.commons.annotation;

import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.kairos.commons.utils.ObjectUtils.newHashSet;
import static com.kairos.enums.wta.WTATemplateType.*;

public class ValidateIgnoreCounterConfiguration implements ConstraintValidator<ValidateIgnoreCounter, WTABaseRuleTemplateDTO> {

    @Override
    public void initialize(ValidateIgnoreCounter constraintAnnotation) {

    }

    @Override
    public boolean isValid(WTABaseRuleTemplateDTO value, ConstraintValidatorContext context) {
        boolean result=true;
        boolean staffCanIgnore = value.getPhaseTemplateValues().stream().anyMatch(phaseTemplateValue -> phaseTemplateValue.isStaffCanIgnore());
        boolean plannerIgnore = value.getPhaseTemplateValues().stream().anyMatch(phaseTemplateValue -> phaseTemplateValue.isManagementCanIgnore());
        if (newHashSet(CHILD_CARE_DAYS_CHECK, VETO_AND_STOP_BRICKS, SENIOR_DAYS_PER_YEAR, WTA_FOR_CARE_DAYS).contains(value.getWtaTemplateType())) {
           result=true;
        }else if(((staffCanIgnore && value.getStaffCanIgnoreCounter() < 1) || (plannerIgnore && value.getManagementCanIgnoreCounter() < 1))) {
            String message = "Counter can not be zero for Staff and management if Ignore checkbox : " + value.getName();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            result =false;
        }
        return result;
    }
}
