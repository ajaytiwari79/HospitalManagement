package com.kairos.persistence.model.activity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.activity.activity_tabs.PhaseSettingsActivityTab;
import com.kairos.enums.ActivityStateEnum;
import com.kairos.persistence.model.activity.tabs.*;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.RulesActivityTab;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by pawanmandhan on 17/8/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "activities")
@Getter
@Setter
@NoArgsConstructor
public class Activity extends MongoBaseEntity implements Serializable {

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
    private GeneralActivityTab generalActivityTab;
    private BalanceSettingsActivityTab balanceSettingsActivityTab;

    private IndividualPointsActivityTab individualPointsActivityTab;

    private Set<BigInteger> childActivityIds=new HashSet<>();
    private NotesActivityTab notesActivityTab;
    private CommunicationActivityTab communicationActivityTab;
    private BonusActivityTab bonusActivityTab;
    private RulesActivityTab rulesActivityTab;
    private TimeCalculationActivityTab timeCalculationActivityTab;
    private SkillActivityTab skillActivityTab;
    private PhaseSettingsActivityTab phaseSettingsActivityTab;
    private OptaPlannerSettingActivityTab optaPlannerSettingActivityTab;
    private CTAAndWTASettingsActivityTab ctaAndWtaSettingsActivityTab;
    private LocationActivityTab locationActivityTab;
    private BigInteger countryParentId;
    @JsonIgnore
    private boolean disabled;
    private BigInteger activityPriorityId;

    //time care id
    private String externalId;

    public Activity(String name, String description, List<BigInteger> tags) {
        this.name = name;
        this.description = description;
        this.tags = tags;

    }


    public Activity(BalanceSettingsActivityTab balanceSettingsActivityTab) {
        this.balanceSettingsActivityTab = balanceSettingsActivityTab;
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
