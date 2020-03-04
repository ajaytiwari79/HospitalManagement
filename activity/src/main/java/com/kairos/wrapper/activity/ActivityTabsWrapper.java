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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by pawanmandhan on 23/8/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class ActivityTabsWrapper {

    private GeneralActivityTabWithTagDTO generalTab;
    private List<ActivityCategory> activityCategories;
    private RulesActivityTab rulesTab;
    private IndividualPointsActivityTab individualPointsTab;
    private TimeCalculationActivityTab timeCalculationActivityTab;
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
    private boolean sicknessSettingValid;


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

    public ActivityTabsWrapper(LocationActivityTab locationActivityTab) {
        this.locationActivityTab = locationActivityTab;
    }

    public ActivityTabsWrapper(NotesActivityTab notesActivityTab) {
        this.notesActivityTab = notesActivityTab;
    }

    public ActivityTabsWrapper(RulesActivityTab rulesTab, List<DayType> dayTypes, List<EmploymentTypeDTO> employmentTypes) {
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

    public ActivityTabsWrapper(GeneralActivityTabWithTagDTO generalTab, List<ActivityCategory> activityCategories) {
        this.generalTab = generalTab;
        this.activityCategories = activityCategories;
    }

    public ActivityTabsWrapper(GeneralActivityTabWithTagDTO generalTab, BigInteger activityId,List<ActivityCategory> activityCategories) {
        this.generalTab = generalTab;
        this.activityId = activityId;
        this.activityCategories=activityCategories;
    }

    public ActivityTabsWrapper(TimeCalculationActivityTab timeCalculationActivityTab, List<DayType> dayTypes,List<Long> rulesTabDayTypes) {
        this.timeCalculationActivityTab = timeCalculationActivityTab;
        this.dayTypes = dayTypes;
        this.rulesTabDayTypes=rulesTabDayTypes;

    }

}
