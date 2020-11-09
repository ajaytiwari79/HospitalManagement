package com.kairos.wrapper.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.activity.activity_tabs.ActivityPhaseSettings;
import com.kairos.dto.activity.activity.activity_tabs.GeneralActivityWithTagDTO;
import com.kairos.dto.activity.presence_type.PresenceTypeWithTimeTypeDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.persistence.model.activity.tabs.*;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.ActivityRulesSettings;
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
public class ActivitySettingsWrapper {

    private GeneralActivityWithTagDTO generalTab;
    private List<ActivityCategory> activityCategories;
    private ActivityRulesSettings rulesTab;
    private ActivityIndividualPointsSettings individualPointsTab;
    private ActivityTimeCalculationSettings activityTimeCalculationSettings;
    private ActivityNotesSettings activityNotesSettings;
    private ActivityCommunicationSettings activityCommunicationSettings;
    private ActivityBonusSettings activityBonusSettings;
    private ActivitySkillSettings activitySkillSettings;
    private ActivityOptaPlannerSetting activityOptaPlannerSetting;
    private ActivityCTAAndWTASettings activityCTAAndWTASettings;
    private BigInteger activityId;
    private PresenceTypeWithTimeTypeDTO presenceTypeWithTimeType;
    private List<DayTypeDTO> dayTypes;
    private List<TimeTypeDTO> timeTypes;
    private ActivityLocationSettings activityLocationSettings;
    private List<EmploymentTypeDTO> employmentTypes;
    private List<BigInteger> rulesTabDayTypes= new ArrayList<>();
    private ActivityPhaseSettings activityPhaseSettings;
    private Set<AccessGroupRole> roles;
    private boolean sicknessSettingValid;


    public ActivitySettingsWrapper(ActivityOptaPlannerSetting activityOptaPlannerSetting) {
        this.activityOptaPlannerSetting = activityOptaPlannerSetting;
    }

    public ActivityIndividualPointsSettings getIndividualPointsTab() {
        return individualPointsTab;
    }

    public ActivitySettingsWrapper(ActivityCTAAndWTASettings activityCTAAndWTASettings) {
        this.activityCTAAndWTASettings = activityCTAAndWTASettings;
    }

    public ActivitySettingsWrapper(ActivitySkillSettings activitySkillSettings) {
        this.activitySkillSettings = activitySkillSettings;
    }

    public ActivitySettingsWrapper(ActivityCommunicationSettings activityCommunicationSettings) {
        this.activityCommunicationSettings = activityCommunicationSettings;
    }

    public ActivitySettingsWrapper(ActivityBonusSettings activityBonusSettings) {
        this.activityBonusSettings = activityBonusSettings;
    }

    public void setIndividualPointsTab(ActivityIndividualPointsSettings individualPointsTab) {
        this.individualPointsTab = individualPointsTab;
    }

    public ActivitySettingsWrapper(ActivityIndividualPointsSettings individualPointsTab) {
        this.individualPointsTab = individualPointsTab;
    }

    public ActivitySettingsWrapper(ActivityRulesSettings rulesTab) {
        this.rulesTab = rulesTab;
    }

    public ActivitySettingsWrapper(ActivityLocationSettings activityLocationSettings) {
        this.activityLocationSettings = activityLocationSettings;
    }

    public ActivitySettingsWrapper(ActivityNotesSettings activityNotesSettings) {
        this.activityNotesSettings = activityNotesSettings;
    }

    public ActivitySettingsWrapper(ActivityRulesSettings rulesTab, List<DayTypeDTO> dayTypes, List<EmploymentTypeDTO> employmentTypes) {
        this.rulesTab = rulesTab;
        this.dayTypes = dayTypes;
        this.employmentTypes=employmentTypes;
    }

    public ActivitySettingsWrapper(Set<AccessGroupRole> accessGroupRoles, ActivityPhaseSettings activityPhaseSettings, List<DayTypeDTO> dayTypes, List<EmploymentTypeDTO> employmentTypes) {
        this.roles=accessGroupRoles;
        this.activityPhaseSettings = activityPhaseSettings;
        this.dayTypes = dayTypes;
        this.employmentTypes=employmentTypes;
    }

    public ActivitySettingsWrapper(GeneralActivityWithTagDTO generalTab, List<ActivityCategory> activityCategories) {
        this.generalTab = generalTab;
        this.activityCategories = activityCategories;
    }

    public ActivitySettingsWrapper(GeneralActivityWithTagDTO generalTab, BigInteger activityId, List<ActivityCategory> activityCategories) {
        this.generalTab = generalTab;
        this.activityId = activityId;
        this.activityCategories=activityCategories;
    }

    public ActivitySettingsWrapper(ActivityTimeCalculationSettings activityTimeCalculationSettings, List<DayTypeDTO> dayTypes, List<BigInteger> rulesTabDayTypes) {
        this.activityTimeCalculationSettings = activityTimeCalculationSettings;
        this.dayTypes = dayTypes;
        this.rulesTabDayTypes=rulesTabDayTypes;

    }

}
