package com.kairos.persistence.model.activity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.ActivityStateEnum;
import com.kairos.persistence.model.activity.tabs.*;
import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by pawanmandhan on 17/8/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "activities")
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
    private RulesActivityTab rulesActivityTab;
    private IndividualPointsActivityTab individualPointsActivityTab;
    private TimeCalculationActivityTab timeCalculationActivityTab;
    private List<CompositeActivity> compositeActivities;

    private NotesActivityTab notesActivityTab;
    private CommunicationActivityTab communicationActivityTab;
    private BonusActivityTab bonusActivityTab;
    private SkillActivityTab skillActivityTab;
    private OptaPlannerSettingActivityTab optaPlannerSettingActivityTab;
    private CTAAndWTASettingsActivityTab ctaAndWtaSettingsActivityTab;
    private LocationActivityTab locationActivityTab;
    private PermissionsActivityTab permissionsActivityTab;
    @JsonIgnore
    private boolean disabled;

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

    public Activity() {
// default constructor
    }


    public List<BigInteger> getTags() {
        return tags;
    }

    public void setTags(List<BigInteger> tags) {
        this.tags = tags;
    }

    public IndividualPointsActivityTab getIndividualPointsActivityTab() {
        return individualPointsActivityTab;
    }

    public void setIndividualPointsActivityTab(IndividualPointsActivityTab individualPointsActivityTab) {
        this.individualPointsActivityTab = individualPointsActivityTab;
    }

    public RulesActivityTab getRulesActivityTab() {
        return rulesActivityTab;
    }

    public void setRulesActivityTab(RulesActivityTab rulesActivityTab) {
        this.rulesActivityTab = rulesActivityTab;
    }

    public GeneralActivityTab getGeneralActivityTab() {
        return generalActivityTab;
    }

    public void setGeneralActivityTab(GeneralActivityTab generalActivityTab) {
        this.generalActivityTab = generalActivityTab;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BalanceSettingsActivityTab getBalanceSettingsActivityTab() {
        return balanceSettingsActivityTab;
    }

    public void setBalanceSettingsActivityTab(BalanceSettingsActivityTab balanceSettingsActivityTab) {
        this.balanceSettingsActivityTab = balanceSettingsActivityTab;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public TimeCalculationActivityTab getTimeCalculationActivityTab() {
        return timeCalculationActivityTab;
    }

    public void setTimeCalculationActivityTab(TimeCalculationActivityTab timeCalculationActivityTab) {
        this.timeCalculationActivityTab = timeCalculationActivityTab;
    }

    public List<CompositeActivity> getCompositeActivities() {
        return compositeActivities =Optional.ofNullable(compositeActivities).orElse(new ArrayList<>());
    }

    public void setCompositeActivities(List<CompositeActivity> compositeActivities) {
        this.compositeActivities = compositeActivities;
    }

    public NotesActivityTab getNotesActivityTab() {
        return notesActivityTab;
    }

    public void setNotesActivityTab(NotesActivityTab notesActivityTab) {
        this.notesActivityTab = notesActivityTab;
    }

    public CommunicationActivityTab getCommunicationActivityTab() {
        return communicationActivityTab;
    }

    public void setCommunicationActivityTab(CommunicationActivityTab communicationActivityTab) {
        this.communicationActivityTab = communicationActivityTab;
    }

    public BonusActivityTab getBonusActivityTab() {
        return bonusActivityTab;
    }

    public void setBonusActivityTab(BonusActivityTab bonusActivityTab) {
        this.bonusActivityTab = bonusActivityTab;
    }

    public SkillActivityTab getSkillActivityTab() {
        return skillActivityTab;
    }

    public void setSkillActivityTab(SkillActivityTab skillActivityTab) {
        this.skillActivityTab = skillActivityTab;
    }

    public List<Long> getExpertises() {
        return expertises;
    }

    public void setExpertises(List<Long> expertises) {
        this.expertises = expertises;
    }

    public List<Long> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<Long> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<Long> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<Long> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public List<Long> getRegions() {
        return regions;
    }

    public void setRegions(List<Long> regions) {
        this.regions = regions;
    }

    public List<Long> getLevels() {
        return levels;
    }

    public void setLevels(List<Long> levels) {
        this.levels = levels;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public List<Long> getEmploymentTypes() {
        return employmentTypes;
    }

    public void setEmploymentTypes(List<Long> employmentTypes) {
        this.employmentTypes = employmentTypes;
    }

    public Activity(List<BigInteger> tags) {
        this.tags = tags;
    }

    public OptaPlannerSettingActivityTab getOptaPlannerSettingActivityTab() {
        return optaPlannerSettingActivityTab;
    }

    public void setOptaPlannerSettingActivityTab(OptaPlannerSettingActivityTab optaPlannerSettingActivityTab) {
        this.optaPlannerSettingActivityTab = optaPlannerSettingActivityTab;
    }

    public CTAAndWTASettingsActivityTab getCtaAndWtaSettingsActivityTab() {
        return ctaAndWtaSettingsActivityTab;
    }

    public void setCtaAndWtaSettingsActivityTab(CTAAndWTASettingsActivityTab ctaAndWtaSettingsActivityTab) {
        this.ctaAndWtaSettingsActivityTab = ctaAndWtaSettingsActivityTab;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public boolean isParentActivity() {
        return isParentActivity;
    }

    public void setParentActivity(boolean parentActivityType) {
        isParentActivity = parentActivityType;
    }

    public BigInteger getParentId() {
        return parentId;
    }

    public void setParentId(BigInteger parentId) {
        this.parentId = parentId;
    }

    public ActivityStateEnum getState() {
        return state;
    }

    public void setState(ActivityStateEnum state) {
        this.state = state;
    }

    public LocationActivityTab getLocationActivityTab() {
        return locationActivityTab;
    }

    public void setLocationActivityTab(LocationActivityTab locationActivityTab) {
        this.locationActivityTab = locationActivityTab;
    }

    public static Activity copyProperties(Activity source, Activity target, String _id, String organizationType, String organizationSubType) {
        BeanUtils.copyProperties(source, target, _id, organizationSubType, organizationType);
        return target;
    }

    public PermissionsActivityTab getPermissionsActivityTab() {
        return permissionsActivityTab;
    }

    public void setPermissionsActivityTab(PermissionsActivityTab permissionsActivityTab) {
        this.permissionsActivityTab = permissionsActivityTab;
    }


    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }


    @Override
    public String toString() {
        return "Activity{" +
                "name='" + name + '\'' +
                "id='" + super.id + '\'' +
                '}';
    }
}
