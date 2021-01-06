package com.kairos.wrapper.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.activity.activity.CompositeActivityDTO;
import com.kairos.dto.activity.activity.activity_tabs.ActivityPhaseSettings;
import com.kairos.dto.activity.activity.activity_tabs.TimeCalculationActivityDTO;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.persistence.model.activity.tabs.ActivityBalanceSettings;
import com.kairos.persistence.model.activity.tabs.ActivityGeneralSettings;
import com.kairos.persistence.model.activity.tabs.ActivitySkillSettings;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.ActivityRulesSettings;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.*;

/**
 * Created by pavan on 8/2/18.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class ActivityWithCompositeDTO {

    private BigInteger id;
    private String name;
    private ActivityGeneralSettings activityGeneralSettings;
    private TimeCalculationActivityDTO activityTimeCalculationSettings;
    private List<CompositeActivityDTO> compositeActivities= new ArrayList<>();
    private List<Long> expertises= new ArrayList<>();
    private List<Long> employmentTypes= new ArrayList<>();
    private ActivityRulesSettings activityRulesSettings;
    private ActivitySkillSettings activitySkillSettings;
    private ActivityPhaseSettings activityPhaseSettings;
    private ActivityBalanceSettings activityBalanceSettings;
    private boolean allowChildActivities;
    private boolean applicableForChildActivities;
    private Long staffId;
    private BigInteger activityId;
    private Long employmentId;
    private Long unitId;
    private Short shortestTime;
    private Short longestTime;
    private Integer minLength;
    private Integer maxThisActivityPerShift;
    private boolean eligibleForMove;
    private LocalTime earliestStartTime;
    private LocalTime latestStartTime;
    private LocalTime maximumEndTime;
    private List<Long> dayTypeIds= new ArrayList<>();
    private Set<BigInteger> childActivityIds=new HashSet<>();
    private Set<BigInteger> availableChildActivityIds =new HashSet<>();
    private BigInteger parentActivityId;
    private int ranking;
    private TimeTypeEnum secondLevelTimtype;
    private int mostlyUsedCount;
    private Map<String, TranslationInfo> translations;

    public String getName(){
        return TranslationUtil.getName(translations,name);
    }
}
