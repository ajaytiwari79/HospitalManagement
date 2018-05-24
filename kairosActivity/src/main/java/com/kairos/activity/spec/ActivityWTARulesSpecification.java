package com.kairos.activity.spec;

import com.kairos.activity.client.dto.TimeSlotWrapper;
import com.kairos.activity.client.dto.staff.StaffAdditionalInfoDTO;
import com.kairos.activity.custom_exception.ActionNotPermittedException;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.custom_exception.DataNotFoundException;
import com.kairos.activity.enums.RuleTemplates;
import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.persistence.model.activity.Shift;
import com.kairos.activity.persistence.model.phase.Phase;


import com.kairos.activity.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.activity.persistence.model.wta.templates.template_types.*;
import com.kairos.activity.persistence.model.wta.wrapper.RuleTemplateSpecificInfo;
import com.kairos.activity.persistence.model.wta.wrapper.RuleTemplateWrapper;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.util.DateUtils;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.activity.util.WTARuleTemplateValidatorUtility;
import com.kairos.response.dto.web.wta.WTAResponseDTO;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static com.kairos.activity.enums.RuleTemplates.getByTemplateType;
/**
 * Created by vipul on 8/2/18.
 */
public class ActivityWTARulesSpecification extends AbstractActivitySpecification<Activity> {
    Logger logger = LoggerFactory.getLogger(ActivityWTARulesSpecification.class);
    private RuleTemplateSpecificInfo ruleTemplateSpecificInfo;
    private List<RuleTemplateWrapper> ruleTemplateWrappers;
    @Autowired
    private ExceptionService exceptionService;


    public ActivityWTARulesSpecification(RuleTemplateSpecificInfo ruleTemplateSpecificInfo,List<RuleTemplateWrapper> ruleTemplateWrappers) {
        this.ruleTemplateSpecificInfo = ruleTemplateSpecificInfo;
        this.ruleTemplateWrappers = ruleTemplateWrappers;
    }



    @Override
    public boolean isSatisfied(Activity activity) {
        ruleTemplateWrappers.forEach(ruleTemplateWrapper -> {
            ruleTemplateWrapper.isSatisfied();
        });

        return true;
    }

    private String getTemplateType(String templateType){
        if(!templateType.contains("_")){
            return templateType;
        }
        int lastCharIndex=templateType.lastIndexOf("_");
        if(lastCharIndex>0){
            char nextCharacter=templateType.charAt(lastCharIndex+1);
            if(!Character.isDigit(templateType.charAt(lastCharIndex+1))){
                return templateType;
            }
            else{
                return templateType.substring(0,lastCharIndex);
            }
        }
        return null;
    }
}
