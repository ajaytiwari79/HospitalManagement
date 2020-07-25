package com.kairos.persistence.model.activity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.annotations.KPermissionField;
import com.kairos.annotations.KPermissionModel;
import com.kairos.annotations.KPermissionSubModel;
import com.kairos.dto.activity.activity.activity_tabs.ActivityPhaseSettings;
import com.kairos.enums.ActivityStateEnum;
import com.kairos.persistence.model.activity.tabs.*;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.ActivityRulesSettings;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by pawanmandhan on 17/8/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true, value = {"path"})
@Document(collection = "activities")
@Getter
@Setter
@NoArgsConstructor
@KPermissionModel
public class Activity extends MongoBaseEntity {

    @KPermissionField
    private String name;
    @KPermissionField
    private String description;
    private Long countryId;
    private List<Long> expertises;
    private List<Long> organizationTypes;
    private List<Long> organizationSubTypes;
    private List<Long> regions;
    private List<Long> levels;
    private List<Long> employmentTypes;
    @KPermissionField
    private List<BigInteger> tags = new ArrayList<>();
    private ActivityStateEnum state = ActivityStateEnum.DRAFT;

    @Indexed
    private Long unitId;
    private BigInteger parentId;
    @JsonIgnore
    private boolean isParentActivity = true;
    @KPermissionSubModel
    private ActivityGeneralSettings activityGeneralSettings;
    private ActivityBalanceSettings activityBalanceSettings;
    @KPermissionSubModel
    private ActivityIndividualPointsSettings activityIndividualPointsSettings;
    private Set<BigInteger> childActivityIds=new HashSet<>();
    @KPermissionSubModel
    private ActivityNotesSettings activityNotesSettings;
    private ActivityCommunicationSettings activityCommunicationSettings;
    @KPermissionSubModel
    private ActivityBonusSettings activityBonusSettings;
    @KPermissionSubModel
    private ActivityRulesSettings activityRulesSettings;
    @KPermissionSubModel
    private ActivityTimeCalculationSettings activityTimeCalculationSettings;
    @KPermissionSubModel
    private ActivitySkillSettings activitySkillSettings;
    private ActivityPhaseSettings activityPhaseSettings;
    private ActivityOptaPlannerSetting activityOptaPlannerSetting;
    private ActivityCTAAndWTASettings activityCTAAndWTASettings;
    private ActivityLocationSettings activityLocationSettings;
    private BigInteger countryParentId;
    @JsonIgnore
    private boolean disabled;
    private BigInteger activityPriorityId;

    //time care id
    private String externalId;
    private String path;

    public Activity(String name, String description, List<BigInteger> tags) {
        this.name = name;
        this.description = description;
        this.tags = tags;
    }




    public Activity(ActivityBalanceSettings activityBalanceSettings) {
        this.activityBalanceSettings = activityBalanceSettings;
    }




    public static Activity copyProperties(Activity source, Activity target, String _id, String organizationType, String organizationSubType) {
        BeanUtils.copyProperties(source, target, _id, organizationSubType, organizationType);
        return target;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "name='" + name + '\'' +
                "id='" + super.id + '\'' +
                '}';
    }
}

