package com.kairos.wrapper.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.presence_type.PresenceTypeWithTimeTypeDTO;
import com.kairos.activity.time_type.TimeTypeDTO;
import com.kairos.persistence.model.activity.tabs.*;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.RulesActivityTab;
import com.kairos.user.country.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.user.country.day_type.DayType;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pawanmandhan on 23/8/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityTabsWrapper {

    private GeneralActivityTab generalTab;
    private List<ActivityCategory> activityCategories;
    private BalanceSettingsActivityTab balanceSettingsTab;
    private RulesActivityTab rulesTab;
    private IndividualPointsActivityTab individualPointsTab;
    private TimeCalculationActivityTab timeCalculationActivityTab;
    private CompositeActivity compositeActivity;
    private NotesActivityTab notesActivityTab;
    private CommunicationActivityTab communicationActivityTab;
    private BonusActivityTab bonusActivityTab;
    private SkillActivityTab skillActivityTab;
    private OptaPlannerSettingActivityTab optaPlannerSettingActivityTab;
    private CTAAndWTASettingsActivityTab ctaAndWtaSettingsActivityTab;
    private BigInteger activityId;
    private PresenceTypeWithTimeTypeDTO presenceTypeWithTimeType;
    private List<DayType> dayTypes;
    private List<TimeTypeDTO> timeTypes;
    private LocationActivityTab locationActivityTab;
    private PermissionsActivityTab permissionsActivityTab;
    private List<EmploymentTypeDTO> employmentTypes;
    private List<Long> rulesTabDayTypes= new ArrayList<>();

    public List<TimeTypeDTO> getTimeTypes() {
        return timeTypes;
    }

    public void setTimeTypes(List<TimeTypeDTO> timeTypes) {
        this.timeTypes = timeTypes;
    }

    public List<DayType> getDayTypes() {
        return dayTypes;
    }

    public void setDayTypes(List<DayType> dayTypes) {
        this.dayTypes = dayTypes;
    }

    public ActivityTabsWrapper(OptaPlannerSettingActivityTab optaPlannerSettingActivityTab) {
        this.optaPlannerSettingActivityTab = optaPlannerSettingActivityTab;
    }

    public IndividualPointsActivityTab getIndividualPointsTab() {
        return individualPointsTab;
    }

    public ActivityTabsWrapper(CTAAndWTASettingsActivityTab ctaAndWtaSettingsActivityTab) {
        this.ctaAndWtaSettingsActivityTab = ctaAndWtaSettingsActivityTab;
    }

    public ActivityTabsWrapper(SkillActivityTab skillActivityTab) {
        this.skillActivityTab = skillActivityTab;
    }

    public ActivityTabsWrapper(CommunicationActivityTab communicationActivityTab) {
        this.communicationActivityTab = communicationActivityTab;
    }

    public ActivityTabsWrapper(BonusActivityTab bonusActivityTab) {
        this.bonusActivityTab = bonusActivityTab;
    }

    public void setIndividualPointsTab(IndividualPointsActivityTab individualPointsTab) {
        this.individualPointsTab = individualPointsTab;
    }

    public ActivityTabsWrapper(IndividualPointsActivityTab individualPointsTab) {
        this.individualPointsTab = individualPointsTab;
    }

    public ActivityTabsWrapper(RulesActivityTab rulesTab) {
        this.rulesTab = rulesTab;
    }

    public ActivityTabsWrapper(RulesActivityTab rulesTab, List<DayType> dayTypes,List<EmploymentTypeDTO> employmentTypes) {
        this.rulesTab = rulesTab;
        this.dayTypes = dayTypes;
        this.employmentTypes=employmentTypes;
    }

    public ActivityTabsWrapper(BalanceSettingsActivityTab balanceSettingsTab) {
        this.balanceSettingsTab = balanceSettingsTab;
    }

    public ActivityTabsWrapper(BalanceSettingsActivityTab balanceSettingsTab, PresenceTypeWithTimeTypeDTO presenceTypeWithTimeType) {
        this.balanceSettingsTab = balanceSettingsTab;
        this.presenceTypeWithTimeType = presenceTypeWithTimeType;
    }

    public ActivityTabsWrapper() {
    }

    public ActivityTabsWrapper(GeneralActivityTab generalTab, List<ActivityCategory> activityCategories) {
        this.generalTab = generalTab;
        this.activityCategories = activityCategories;
    }

    public ActivityTabsWrapper(GeneralActivityTab generalTab, BigInteger activityId) {
        this.generalTab = generalTab;
        this.activityId = activityId;
    }
    public ActivityTabsWrapper(GeneralActivityTab generalTab, BigInteger activityId,List<ActivityCategory> activityCategories) {
        this.generalTab = generalTab;
        this.activityId = activityId;
        this.activityCategories=activityCategories;
    }
    public ActivityTabsWrapper(TimeCalculationActivityTab timeCalculationActivityTab) {
        this.timeCalculationActivityTab = timeCalculationActivityTab;

    }

    public ActivityTabsWrapper(TimeCalculationActivityTab timeCalculationActivityTab, List<DayType> dayTypes,List<Long> rulesTabDayTypes) {
        this.timeCalculationActivityTab = timeCalculationActivityTab;
        this.dayTypes = dayTypes;
        this.rulesTabDayTypes=rulesTabDayTypes;

    }



    public ActivityTabsWrapper(LocationActivityTab locationActivityTab) {
        this.locationActivityTab = locationActivityTab;
    }


    public ActivityTabsWrapper(NotesActivityTab notesActivityTab) {
        this.notesActivityTab = notesActivityTab;
    }

    public GeneralActivityTab getGeneralTab() {
        return generalTab;
    }

    public void setGeneralTab(GeneralActivityTab generalTab) {
        this.generalTab = generalTab;
    }

    public List<ActivityCategory> getActivityCategories() {
        return activityCategories;
    }

    public void setActivityCategories(List<ActivityCategory> activityCategories) {
        this.activityCategories = activityCategories;
    }

    public BalanceSettingsActivityTab getBalanceSettingsTab() {
        return balanceSettingsTab;
    }

    public void setBalanceSettingsTab(BalanceSettingsActivityTab balanceSettingsTab) {
        this.balanceSettingsTab = balanceSettingsTab;
    }


    public RulesActivityTab getRulesTab() {
        return rulesTab;
    }

    public void setRulesTab(RulesActivityTab rulesTab) {
        this.rulesTab = rulesTab;
    }

    public TimeCalculationActivityTab getTimeCalculationActivityTab() {
        return timeCalculationActivityTab;
    }

    public void setTimeCalculationActivityTab(TimeCalculationActivityTab timeCalculationActivityTab) {
        this.timeCalculationActivityTab = timeCalculationActivityTab;

    }

    public CompositeActivity getCompositeActivity() {
        return compositeActivity;
    }

    public void setCompositeActivity(CompositeActivity compositeActivity) {
        this.compositeActivity = compositeActivity;
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

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public PresenceTypeWithTimeTypeDTO getPresenceTypeWithTimeType() {
        return presenceTypeWithTimeType;
    }

    public void setPresenceTypeWithTimeType(PresenceTypeWithTimeTypeDTO presenceTypeWithTimeType) {
        this.presenceTypeWithTimeType = presenceTypeWithTimeType;
    }

    public LocationActivityTab getLocationActivityTab() {
        return locationActivityTab;
    }

    public void setLocationActivityTab(LocationActivityTab locationActivityTab) {
        this.locationActivityTab = locationActivityTab;
    }

    public PermissionsActivityTab getPermissionsActivityTab() {
        return permissionsActivityTab;
    }

    public void setPermissionsActivityTab(PermissionsActivityTab permissionsActivityTab) {
        this.permissionsActivityTab = permissionsActivityTab;
    }
    public ActivityTabsWrapper(PermissionsActivityTab permissionsActivityTab) {
        this.permissionsActivityTab = permissionsActivityTab;
    }

    public List<EmploymentTypeDTO> getEmploymentTypes() {
        return employmentTypes;
    }

    public void setEmploymentTypes(List<EmploymentTypeDTO> employmentTypes) {
        this.employmentTypes = employmentTypes;
    }
}
