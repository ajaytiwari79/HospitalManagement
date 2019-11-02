package com.kairos.persistence.model.activity;

import com.kairos.dto.activity.activity.activity_tabs.PhaseSettingsActivityTab;
import com.kairos.enums.OrganizationHierarchy;
import com.kairos.enums.PriorityFor;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.TimeTypes;
import com.kairos.persistence.model.activity.tabs.SkillActivityTab;
import com.kairos.persistence.model.activity.tabs.TimeCalculationActivityTab;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.RulesActivityTab;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.kairos.enums.PriorityFor.PRESENCE;

@Document(collection = "time_Type")
@Getter
@Setter
@NoArgsConstructor
public class TimeType extends MongoBaseEntity{

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
    private RulesActivityTab rulesActivityTab;
    private TimeCalculationActivityTab timeCalculationActivityTab;
    private SkillActivityTab skillActivityTab;
    private PhaseSettingsActivityTab phaseSettingsActivityTab;
    private List<Long> expertises;
    private List<Long> organizationTypes;
    private List<Long> organizationSubTypes;
    private List<Long> regions;
    private List<Long> levels;
    private List<Long> employmentTypes;
    private boolean breakNotHeldValid;
    private BigInteger activityPriorityId;
    private PriorityFor priorityFor = PRESENCE;

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
