package com.kairos.wrapper.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.activity.CompositeActivityDTO;
import com.kairos.dto.activity.activity.activity_tabs.PhaseSettingsActivityTab;
import com.kairos.dto.activity.activity.activity_tabs.TimeCalculationActivityDTO;
import com.kairos.persistence.model.activity.tabs.BalanceSettingsActivityTab;
import com.kairos.persistence.model.activity.tabs.GeneralActivityTab;
import com.kairos.persistence.model.activity.tabs.SkillActivityTab;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.RulesActivityTab;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private GeneralActivityTab generalActivityTab;
    private TimeCalculationActivityDTO timeCalculationActivityTab;
    private List<CompositeActivityDTO> compositeActivities= new ArrayList<>();
    private List<Long> expertises= new ArrayList<>();
    private List<Long> employmentTypes= new ArrayList<>();
    private RulesActivityTab rulesActivityTab;
    private SkillActivityTab skillActivityTab;
    private PhaseSettingsActivityTab phaseSettingsActivityTab;
    private BalanceSettingsActivityTab balanceSettingsActivityTab;
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
    private String activityPriorityName;

}
