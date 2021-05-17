package com.kairos.persistence.model.activity;

import com.kairos.dto.activity.activity.activity_tabs.ActivityPhaseSettings;
import com.kairos.enums.*;
import com.kairos.persistence.model.activity.tabs.ActivitySkillSettings;
import com.kairos.persistence.model.activity.tabs.ActivityTimeCalculationSettings;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.ActivityRulesSettings;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.enums.PriorityFor.NONE;
import static com.kairos.enums.PriorityFor.PRESENCE;

@Document(collection = "time_Type")
@Getter
@Setter
@NoArgsConstructor
public class TimeType extends MongoBaseEntity implements Serializable {

    private static final long serialVersionUID = 3265660403399363722L;
    private Long countryId;
    private TimeTypes timeTypes;
    private BigInteger upperLevelTimeTypeId;
    private String label;
    private boolean leafNode;
    private String description;
    private List<BigInteger> childTimeTypeIds = new ArrayList<>();
    private String backgroundColor;
    private TimeTypeEnum secondLevelType;
    private Set<OrganizationHierarchy> activityCanBeCopiedForOrganizationHierarchy;
    private boolean partOfTeam;
    private boolean allowChildActivities;
    private boolean allowedConflicts;
    private ActivityRulesSettings activityRulesSettings;
    private ActivityTimeCalculationSettings activityTimeCalculationSettings;
    private ActivitySkillSettings activitySkillSettings;
    private ActivityPhaseSettings activityPhaseSettings;
    private List<Long> expertises;
    private List<Long> organizationTypes;
    private List<Long> organizationSubTypes;
    private List<Long> regions;
    private List<Long> levels;
    private List<Long> employmentTypes;
    private boolean breakNotHeldValid;
    private PriorityFor priorityFor = NONE;
    private boolean sicknessSettingValid;
    private Map<String,BigInteger> upperLevelTimeTypeDetails;
    //this setting for unity graph
    private UnityActivitySetting unityActivitySetting;

    public TimeType(TimeTypes timeTypes, String label, String description,String backgroundColor,TimeTypeEnum secondLevelType,Long countryId,Set<OrganizationHierarchy> activityCanBeCopiedForOrganizationHierarchy) {
        this.timeTypes = timeTypes;
        this.label = label;
        this.description = description;
        this.backgroundColor=backgroundColor;
        this.leafNode = true;
        this.secondLevelType=secondLevelType;
        this.countryId=countryId;
        this.activityCanBeCopiedForOrganizationHierarchy = activityCanBeCopiedForOrganizationHierarchy;
    }

}
