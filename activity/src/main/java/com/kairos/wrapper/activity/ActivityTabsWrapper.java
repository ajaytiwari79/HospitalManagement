package com.kairos.wrapper.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.activity.activity_tabs.GeneralActivityTabWithTagDTO;
import com.kairos.dto.activity.activity.activity_tabs.PhaseSettingsActivityTab;
import com.kairos.dto.activity.presence_type.PresenceTypeWithTimeTypeDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.dto.user.country.day_type.DayType;
import com.kairos.persistence.model.activity.tabs.*;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.RulesActivityTab;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by pawanmandhan on 23/8/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityTabsWrapper {

    private GeneralActivityTabWithTagDTO generalTab;
    private List<ActivityCategory> activityCategories;
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
    private List<EmploymentTypeDTO> employmentTypes;
    private List<Long> rulesTabDayTypes= new ArrayList<>();
    private PhaseSettingsActivityTab phaseSettingsActivityTab;
    private Set<AccessGroupRole> roles;


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

    public ActivityTabsWrapper(Set<AccessGroupRole> accessGroupRoles,PhaseSettingsActivityTab phaseSettingsActivityTab, List<DayType> dayTypes, List<EmploymentTypeDTO> employmentTypes) {
        this.roles=accessGroupRoles;
        this.phaseSettingsActivityTab = phaseSettingsActivityTab;
        this.dayTypes = dayTypes;
        this.employmentTypes=employmentTypes;
    }

    public ActivityTabsWrapper() {
    }

    public ActivityTabsWrapper(GeneralActivityTabWithTagDTO generalTab, List<ActivityCategory> activityCategories) {
        this.generalTab = generalTab;
        this.activityCategories = activityCategories;
    }

    public ActivityTabsWrapper(GeneralActivityTabWithTagDTO generalTab, BigInteger activityId,List<ActivityCategory> activityCategories) {
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

    public GeneralActivityTabWithTagDTO getGeneralTab() {
        return generalTab;
    }

    public void setGeneralTab(GeneralActivityTabWithTagDTO generalTab) {
        this.generalTab = generalTab;
    }

    public List<ActivityCategory> getActivityCategories() {
        return activityCategories;
    }

    public void setActivityCategories(List<ActivityCategory> activityCategories) {
        this.activityCategories = activityCategories;
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

    public List<EmploymentTypeDTO> getEmploymentTypes() {
        return employmentTypes;
    }

    public void setEmploymentTypes(List<EmploymentTypeDTO> employmentTypes) {
        this.employmentTypes = employmentTypes;
    }

    public List<Long> getRulesTabDayTypes() {
        return rulesTabDayTypes;
    }

    public void setRulesTabDayTypes(List<Long> rulesTabDayTypes) {
        this.rulesTabDayTypes = rulesTabDayTypes;
    }

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

    public PhaseSettingsActivityTab getPhaseSettingsActivityTab() {
        return phaseSettingsActivityTab;
    }

    public void setPhaseSettingsActivityTab(PhaseSettingsActivityTab phaseSettingsActivityTab) {
        this.phaseSettingsActivityTab = phaseSettingsActivityTab;
    }

    public Set<AccessGroupRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<AccessGroupRole> roles) {
        this.roles = roles;
    }
}
