package com.kairos.wrapper.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.activity.TranslationInfo;
import com.kairos.dto.activity.activity.activity_tabs.CompositeShiftActivityDTO;
import com.kairos.dto.user.country.tag.TagDTO;
import com.kairos.enums.ActivityStateEnum;
import com.kairos.enums.OrganizationHierarchy;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.tabs.BalanceSettingsActivityTab;
import com.kairos.persistence.model.activity.tabs.GeneralActivityTab;
import com.kairos.persistence.model.activity.tabs.TimeCalculationActivityTab;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.RulesActivityTab;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by prerna on 16/12/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ActivityTagDTO {
    private BigInteger id;
    private String name;
    private String description;
    private Long countryId;
    private BigInteger categoryId;
    private String categoryName;
    private List<TagDTO> tags = new ArrayList<>();
    private Long unitId;
    private boolean isParentActivity = true;
    private GeneralActivityTab generalActivityTab;
    private BalanceSettingsActivityTab balanceSettingsActivityTab;
    private LocalDate startDate;
    private LocalDate endDate;
    private TimeCalculationActivityTab timeCalculationActivityTab;
    private List<Long> dayTypes= new ArrayList<>();
    private RulesActivityTab rulesActivityTab;
    private Boolean activityCanBeCopied=false;
    private Set<OrganizationHierarchy> activityCanBeCopiedForOrganizationHierarchy;
    private Long parentId;
    private ActivityStateEnum state;
    private List<CompositeShiftActivityDTO> compositeActivities;
    private BigInteger activityPriorityId;
    private boolean allowChildActivities;
    private boolean applicableForChildActivities;
    private boolean sicknessSettingValid;
    private Set<BigInteger> childActivityIds=new HashSet<>();
    // for filter FullDay and Full week activity
    private String methodForCalculatingTime;

    private Map<String, TranslationInfo> translations ;

    public ActivityTagDTO() {
        //default constructor
    }

    public TimeCalculationActivityTab getTimeCalculationActivityTab() {
        return timeCalculationActivityTab;
    }

    public void setTimeCalculationActivityTab(TimeCalculationActivityTab timeCalculationActivityTab) {
        this.timeCalculationActivityTab = timeCalculationActivityTab;
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
        this.name = name;
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

    public List<TagDTO> getTags() {
        return tags;
    }

    public void setTags(List<TagDTO> tags) {
        this.tags = tags;
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

    public void setParentActivity(boolean parentActivity) {
        isParentActivity = parentActivity;
    }

    public GeneralActivityTab getGeneralActivityTab() {
        return generalActivityTab;
    }

    public void setGeneralActivityTab(GeneralActivityTab generalActivityTab) {
        this.generalActivityTab = generalActivityTab;
    }


    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public ActivityStateEnum getState() {
        return state;
    }

    public void setState(ActivityStateEnum state) {
        this.state = state;
    }



    public BalanceSettingsActivityTab getBalanceSettingsActivityTab() {
        return balanceSettingsActivityTab;
    }

    public void setBalanceSettingsActivityTab(BalanceSettingsActivityTab balanceSettingsActivityTab) {
        this.balanceSettingsActivityTab = balanceSettingsActivityTab;
    }

    public List<Long> getDayTypes() {
        return dayTypes;
    }

    public void setDayTypes(List<Long> dayTypes) {
        this.dayTypes = dayTypes;
    }

    public RulesActivityTab getRulesActivityTab() {
        return rulesActivityTab;
    }

    public void setRulesActivityTab(RulesActivityTab rulesActivityTab) {
        this.rulesActivityTab = rulesActivityTab;
    }

    public Boolean isActivityCanBeCopied() {
        return activityCanBeCopied;
    }

    public void setActivityCanBeCopied(Boolean activityCanBeCopied) {
        this.activityCanBeCopied = activityCanBeCopied==null?false:activityCanBeCopied;
    }

    public Set<OrganizationHierarchy> getActivityCanBeCopiedForOrganizationHierarchy() {
        return activityCanBeCopiedForOrganizationHierarchy;
    }

    public void setActivityCanBeCopiedForOrganizationHierarchy(Set<OrganizationHierarchy> activityCanBeCopiedForOrganizationHierarchy) {
        this.activityCanBeCopiedForOrganizationHierarchy = activityCanBeCopiedForOrganizationHierarchy;
    }


    public List<CompositeShiftActivityDTO> getCompositeActivities() {
        return compositeActivities;
    }

    public void setCompositeActivities(List<CompositeShiftActivityDTO> compositeActivities) {
        this.compositeActivities = compositeActivities;
    }

    public BigInteger getActivityPriorityId() {
        return activityPriorityId;
    }

    public void setActivityPriorityId(BigInteger activityPriorityId) {
        this.activityPriorityId = activityPriorityId;
    }

    public boolean isAllowChildActivities() {
        return allowChildActivities;
    }

    public void setAllowChildActivities(boolean allowChildActivities) {
        this.allowChildActivities = allowChildActivities;
    }

    public Set<BigInteger> getChildActivityIds() {
        return childActivityIds;
    }

    public void setChildActivityIds(Set<BigInteger> childActivityIds) {
        this.childActivityIds = childActivityIds;
    }

    public boolean isApplicableForChildActivities() {
        return applicableForChildActivities;
    }

    public void setApplicableForChildActivities(boolean applicableForChildActivities) {
        this.applicableForChildActivities = applicableForChildActivities;
    }

    public String getMethodForCalculatingTime() {
        return methodForCalculatingTime;
    }

    public void setMethodForCalculatingTime(String methodForCalculatingTime) {
        this.methodForCalculatingTime = methodForCalculatingTime;
    }

    public Boolean getActivityCanBeCopied() {
        return activityCanBeCopied;
    }

    public boolean isSicknessSettingValid() {
        return sicknessSettingValid;
    }

    public void setSicknessSettingValid(boolean sicknessSettingValid) {
        this.sicknessSettingValid = sicknessSettingValid;
    }

    public ActivityTagDTO buildActivityTagDTO(Activity activity, List<TagDTO> tags) {
        this.id = activity.getId();
        this.name = activity.getName();
        this.description = activity.getDescription();
        this.isParentActivity = activity.isParentActivity();
        this.unitId = activity.getUnitId();
        this.tags = tags;
        this.state = activity.getState();

        return this;
    }
}
