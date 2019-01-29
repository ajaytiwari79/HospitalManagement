package com.kairos.rule_validator.activity;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.service.exception.ExceptionService;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

/**
 * Created by vipul on 8/2/18.
 */
public class ActivityWTARulesSpecification extends AbstractActivitySpecification<Activity> {
    Logger logger = LoggerFactory.getLogger(ActivityWTARulesSpecification.class);
    private WTAResponseDTO wtaResponseDTO;
    private Shift shift;
    private Phase phase;
    private StaffAdditionalInfoDTO staffAdditionalInfoDTO;
    @Autowired
    private ExceptionService exceptionService;

    public ActivityWTARulesSpecification(WTAResponseDTO wtaResponseDTO, Phase phase, Shift shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        this.wtaResponseDTO = wtaResponseDTO;
        this.shift = shift;
        this.phase = phase;
        this.staffAdditionalInfoDTO = staffAdditionalInfoDTO;
    }

    @Override
    public boolean isSatisfied(Activity activity) {
        if (wtaResponseDTO.getEndDate()!=null && DateUtils.asDate(wtaResponseDTO.getEndDate()).before(shift.getEndDate())) {
            exceptionService.actionNotPermittedException("message.wta.expired-unit");
        }
        return true;
    }

    @Override
    public void validateRules(Activity activity) {

    }

    @Override
    public List<String> isSatisfiedString(Activity activity) {
        return Collections.emptyList();
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
