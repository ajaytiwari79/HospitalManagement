package com.kairos.persistence.model.open_shift;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.activity.open_shift.ActivitiesPerTimeType;
import com.kairos.dto.activity.open_shift.PlannerNotificationInfo;
import com.kairos.dto.activity.open_shift.Priority;
import com.kairos.dto.activity.open_shift.ShiftAssignmentCriteria;
import com.kairos.enums.AllowedLength;
import com.kairos.enums.OpenShiftRuleTemplateType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class OpenShiftRuleTemplateDTO {
    private BigInteger id;
    private String name;
    private Long organizationTypeId;
    private Long organizationSubTypeId;
    private OpenShiftRuleTemplateType ruleTemplateType;
    private List<ActivitiesPerTimeType> activitiesPerTimeTypes;
    private List<Long> selectedSkills;
    private boolean promptPlanner;
    private Long unitId;
    private Long countryId;
    private BigInteger parentId;
    private FeatureRules featureRules;
    private NotificationWay notificationWay;
    private AllowedLength allowedLength;
    private Integer minimumShiftHours;
    private Integer maximumShiftHours;
    private ShiftAssignmentCriteria shiftAssignmentCriteria;
    private BigInteger openShiftIntervalId;
    private PlannerNotificationInfo plannerNotificationInfo;
    private Priority priority;
    private OpenShiftInterval openShiftInterval;
    private  Map<String, TranslationInfo> translations;


    public OpenShiftRuleTemplateDTO() {
        //Default Constructor
    }

    public String getName() {
        return TranslationUtil.getName(translations,name);
    }


}
