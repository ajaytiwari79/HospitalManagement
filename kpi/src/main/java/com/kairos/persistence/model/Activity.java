package com.kairos.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kairos.dto.activity.activity.activity_tabs.*;
import com.kairos.enums.ActivityStateEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class Activity {

    private static final long serialVersionUID = -4888258469348591990L;
    private String name;
    private String description;
    private Long countryId;
    private List<Long> expertises;
    private List<Long> organizationTypes;
    private List<Long> organizationSubTypes;
    private List<Long> regions;
    private List<Long> levels;
    private List<Long> employmentTypes;
    private List<BigInteger> tags = new ArrayList<>();
    private ActivityStateEnum state = ActivityStateEnum.DRAFT;

    @Indexed
    private Long unitId;
    private BigInteger parentId;
    @JsonIgnore
    private boolean isParentActivity = true;
    private ActivityGeneralSettingsDTO activityGeneralSettings;
    private ActivityBalanceSettingDTO activityBalanceSettings;
    private ActivityIndividualPointsSettingsDTO activityIndividualPointsSettings;
    private Set<BigInteger> childActivityIds=new HashSet<>();
    private ActivityRulesSettingsDTO activityRulesSettings;
    private TimeCalculationActivityDTO activityTimeCalculationSettings;
    private ActivityPhaseSettings activityPhaseSettings;
    private BigInteger countryParentId;
    @JsonIgnore
    private boolean disabled;
    @JsonIgnore
    private boolean isChildActivity = false;
    //time care id
    private String externalId;
    private String path;
}
