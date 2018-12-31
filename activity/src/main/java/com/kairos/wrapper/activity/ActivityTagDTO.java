package com.kairos.wrapper.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.ActivityStateEnum;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.tabs.*;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.RulesActivityTab;
import com.kairos.dto.user.country.tag.TagDTO;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by prerna on 16/12/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
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

    private Long parentId;
    private ActivityStateEnum state;

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
