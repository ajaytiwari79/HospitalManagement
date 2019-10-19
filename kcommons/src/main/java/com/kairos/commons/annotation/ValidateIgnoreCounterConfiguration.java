package com.kairos.commons.annotation;

import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.dto.activity.wta.templates.PhaseTemplateValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.commons.utils.ObjectUtils.newHashSet;
import static com.kairos.enums.wta.WTATemplateType.*;

public class ValidateIgnoreCounterConfiguration implements ConstraintValidator<ValidateIgnoreCounter, List<WTABaseRuleTemplateDTO>> {

    @Override
    public void initialize(ValidateIgnoreCounter constraintAnnotation) {
        //It's implemented
    }

    @Override
    public boolean isValid(List<WTABaseRuleTemplateDTO> wtaBaseRuleTemplateDTOS, ConstraintValidatorContext context) {
        StringBuffer message = new StringBuffer("Counter can not be zero for Staff and management if Ignore checkbox is marked as true for below templates: ");
        boolean result = true;
        for (WTABaseRuleTemplateDTO wtaBaseRuleTemplateDTO : wtaBaseRuleTemplateDTOS) {
            boolean staffCanIgnore = wtaBaseRuleTemplateDTO.getPhaseTemplateValues().stream().anyMatch(PhaseTemplateValue::isStaffCanIgnore);
            boolean plannerIgnore = wtaBaseRuleTemplateDTO.getPhaseTemplateValues().stream().anyMatch(PhaseTemplateValue::isManagementCanIgnore);
            if (!newHashSet(CHILD_CARE_DAYS_CHECK, VETO_AND_STOP_BRICKS, SENIOR_DAYS_PER_YEAR, WTA_FOR_CARE_DAYS).contains(wtaBaseRuleTemplateDTO.getWtaTemplateType()) &&
                    ((staffCanIgnore && (isNull(wtaBaseRuleTemplateDTO.getStaffCanIgnoreCounter()) || wtaBaseRuleTemplateDTO.getStaffCanIgnoreCounter() < 1)) || (plannerIgnore && (isNull(wtaBaseRuleTemplateDTO.getManagementCanIgnoreCounter()) ||wtaBaseRuleTemplateDTO.getManagementCanIgnoreCounter() < 1)))) {
                message = message.append(wtaBaseRuleTemplateDTO.getName()+",");
                result = false;
            }
        }
        context.buildConstraintViolationWithTemplate(message.toString()).addConstraintViolation();
        return result;
    }
}
