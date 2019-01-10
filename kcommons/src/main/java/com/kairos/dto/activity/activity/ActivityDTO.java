package com.kairos.dto.activity.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.activity.activity_tabs.*;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by prerna on 6/12/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityDTO {
    private BigInteger id;
    @NotBlank(message = "message.activity.name.notEmpty")
    private String name;
    private List<Long> expertises;
    private String description;
    private Long countryId;
    private BigInteger categoryId;
    private String categoryName;
    private Long unitId = -1L;
    private List<Long> employmentTypes;
    private boolean isParentActivity = true;
    private GeneralActivityTabDTO generalActivityTab;
    private TimeTypeDTO timeType;
    private TimeCalculationActivityDTO timeCalculationActivityTab;
    private RulesActivityTabDTO rulesActivityTab;
    private List<ActivityDTO> compositeActivities;

    private BalanceSettingActivityTabDTO balanceSettingsActivityTab;
    private Long countryActivityId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigInteger parentId;
    private PhaseSettingsActivityTab phaseSettingsActivityTab;
    private List<Long> skills;
    private SkillActivityDTO skillActivityTab;
    private boolean activityCanBeCopied;

    //    private List<Tag> tags;
//    private List<BigInteger> tags = new ArrayList<>();
    private List<BigInteger> tags = new ArrayList<>();

    public ActivityDTO() {
        //default constructor
    }

    public ActivityDTO(BigInteger id, String name, BigInteger parentId) {
        this.id = id;
        this.name = StringUtils.trim(name);
        this.parentId = parentId;
    }

    public ActivityDTO(String name, String description, Long countryId, String categoryName, Long unitId, boolean isParentActivity) {
        this.name = StringUtils.trim(name);
        this.description = StringUtils.trim(description);
        this.countryId = countryId;
        this.categoryName = categoryName;
        this.unitId = unitId;
        this.isParentActivity = isParentActivity;
    }

    public List<Long> getEmploymentTypes() {
        return employmentTypes;
    }

    public void setEmploymentTypes(List<Long> employmentTypes) {
        this.employmentTypes = employmentTypes;
    }

    public RulesActivityTabDTO getRulesActivityTab() {
        return rulesActivityTab;
    }

    public void setRulesActivityTab(RulesActivityTabDTO rulesActivityTab) {
        this.rulesActivityTab = rulesActivityTab;
    }

    public BigInteger getParentId() {
        return parentId;
    }

    public void setParentId(BigInteger parentId) {
        this.parentId = parentId;
    }


    public TimeTypeDTO getTimeType() {
        return timeType;
    }

    public void setTimeType(TimeTypeDTO timeType) {
        this.timeType = timeType;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = StringUtils.trim(name);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = StringUtils.trim(description);
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public BigInteger getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(BigInteger categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<BigInteger> getTags() {
        return this.tags;
    }


    public void setTags(List<BigInteger> tags) {
        this.tags = tags;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getCountryActivityId() {
        return countryActivityId;
    }

    public void setCountryActivityId(Long countryActivityId) {
        this.countryActivityId = countryActivityId;
    }

    public boolean isParentActivity() {
        return isParentActivity;
    }

    public void setParentActivity(boolean parentActivity) {
        isParentActivity = parentActivity;
    }


    public List<ActivityDTO> getCompositeActivities() {
        return compositeActivities;
    }

    public void setCompositeActivities(List<ActivityDTO> compositeActivities) {
        this.compositeActivities = compositeActivities;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<Long> getExpertises() {
        return expertises;
    }

    public void setExpertises(List<Long> expertises) {
        this.expertises = expertises;
    }

    public GeneralActivityTabDTO getGeneralActivityTab() {
        return generalActivityTab;
    }

    public void setGeneralActivityTab(GeneralActivityTabDTO generalActivityTab) {
        this.generalActivityTab = generalActivityTab;
    }

    public TimeCalculationActivityDTO getTimeCalculationActivityTab() {
        return timeCalculationActivityTab;
    }

    public void setTimeCalculationActivityTab(TimeCalculationActivityDTO timeCalculationActivityTab) {
        this.timeCalculationActivityTab = timeCalculationActivityTab;
    }

    public BalanceSettingActivityTabDTO getBalanceSettingsActivityTab() {
        return balanceSettingsActivityTab;
    }

    public void setBalanceSettingsActivityTab(BalanceSettingActivityTabDTO balanceSettingsActivityTab) {
        this.balanceSettingsActivityTab = balanceSettingsActivityTab;
    }

    public PhaseSettingsActivityTab getPhaseSettingsActivityTab() {
        return phaseSettingsActivityTab;
    }

    public void setPhaseSettingsActivityTab(PhaseSettingsActivityTab phaseSettingsActivityTab) {
        this.phaseSettingsActivityTab = phaseSettingsActivityTab;
    }

    public List<Long> getSkills() {
        return skills;
    }

    public void setSkills(List<Long> skills) {
        this.skills = skills;
    }

    public SkillActivityDTO getSkillActivityTab() {
        return skillActivityTab;
    }

    public void setSkillActivityTab(SkillActivityDTO skillActivityTab) {
        this.skillActivityTab = skillActivityTab;
    }

    public boolean isActivityCanBeCopied() {
        return activityCanBeCopied;
    }

    public void setActivityCanBeCopied(boolean activityCanBeCopied) {
        this.activityCanBeCopied = activityCanBeCopied;
    }
}


