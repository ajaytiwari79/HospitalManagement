package com.kairos.dto.activity.time_type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.activity.activity_tabs.ActivityRulesSettingsDTO;
import com.kairos.enums.OrganizationHierarchy;
import com.kairos.enums.PriorityFor;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.UnityActivitySetting;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class TimeTypeDTO implements Serializable {
    private BigInteger id;
    private String timeTypes;
    private String label;
    private String description;
    private BigInteger upperLevelTimeTypeId;
    private boolean selected;
    private List<TimeTypeDTO> children = new ArrayList<>();
    private String backgroundColor;
    private TimeTypeEnum secondLevelType;
    private Set<OrganizationHierarchy> activityCanBeCopiedForOrganizationHierarchy;
    private boolean partOfTeam;
    private boolean allowChildActivities;
    private List<TimeTypeDTO> parent = new ArrayList<>();
    private boolean allowedConflicts;
    private boolean breakNotHeldValid;
    private BigInteger activityPriorityId;
    private PriorityFor priorityFor;
    private boolean sicknessSettingValid;
    private ActivityRulesSettingsDTO activityRulesSettings;
    //this setting for unity graph
    private UnityActivitySetting unityActivitySetting;


    public TimeTypeDTO(String backgroundColor, boolean sicknessSettingValid, ActivityRulesSettingsDTO activityRulesSettings) {
        this.backgroundColor = backgroundColor;
        this.sicknessSettingValid = sicknessSettingValid;
        this.activityRulesSettings = activityRulesSettings;
    }

    public TimeTypeDTO(String timeTypes, String backgroundColor) {
        this.timeTypes = timeTypes;
        this.backgroundColor = backgroundColor;
    }

    public TimeTypeDTO(BigInteger id, String timeTypes, BigInteger upperLevelTimeTypeId) {
        this.id = id;
        this.timeTypes = timeTypes;
        this.upperLevelTimeTypeId = upperLevelTimeTypeId;
    }

}
