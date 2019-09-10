package com.kairos.commons.annotation;

import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class ValidateIgnoreCounterConfiguration implements ConstraintValidator<ValidateIgnoreCounter, List<WTABaseRuleTemplateDTO>> {
    @Override
    public void initialize(ValidateIgnoreCounter constraintAnnotation) {

    }

    @Override
    public boolean isValid(List<WTABaseRuleTemplateDTO> value, ConstraintValidatorContext context) {
        List<ObjectError> fieldErrors=new ArrayList<>();
        WTABaseRuleTemplateDTO wtaBaseRuleTemplateDTO=(WTABaseRuleTemplateDTO)value;
        boolean staffCanIgnore=wtaBaseRuleTemplateDTO.getPhaseTemplateValues().stream().anyMatch(phaseTemplateValue -> phaseTemplateValue.isStaffCanIgnore());
        boolean plannerIgnore=wtaBaseRuleTemplateDTO.getPhaseTemplateValues().stream().anyMatch(phaseTemplateValue -> phaseTemplateValue.isManagementCanIgnore());
        if(staffCanIgnore && wtaBaseRuleTemplateDTO.getStaffCanIgnoreCounter()<1){
         fieldErrors.add(new FieldError("","",wtaBaseRuleTemplateDTO.getName()+"staff counter value"));
        }
        if(plannerIgnore && wtaBaseRuleTemplateDTO.getManagementCanIgnoreCounter()<1){
            fieldErrors.add(new FieldError("","",wtaBaseRuleTemplateDTO.getName()+"management counter value"));
        }
        if(!fieldErrors.isEmpty()){
            throw new MethodArgumentNotValidException();
        }
        return true;
    }
}
