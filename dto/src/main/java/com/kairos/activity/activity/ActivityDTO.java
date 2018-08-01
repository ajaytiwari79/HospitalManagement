package com.kairos.activity.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.activity.activity_tabs.BalanceSettingActivityTabDTO;
import com.kairos.activity.activity.activity_tabs.GeneralActivityTabDTO;
import com.kairos.activity.activity.activity_tabs.PermissionsActivityTabDTO;
import com.kairos.activity.activity.activity_tabs.TimeCalculationActivityDTO;
import com.kairos.activity.time_type.TimeTypeDTO;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Created by prerna on 6/12/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityDTO {
    private BigInteger id;
    private String name;
    private List<Long> expertises;
    private String description;
    private Long countryId;
    private BigInteger categoryId;
    private String categoryName;
    private Long unitId = -1L;
    private boolean isParentActivity = true;
    private GeneralActivityTabDTO generalActivityTab;
    private TimeCalculationActivityDTO timeCalculationActivityTab;
    private List<ActivityDTO> compositeActivities;

    private BalanceSettingActivityTabDTO balanceSettingsActivityTab;
    private Long countryActivityId;
    private TimeTypeDTO timeType;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigInteger parentId;
    private PermissionsActivityTabDTO permissionsActivityTab;

    //    private List<Tag> tags;
//    private List<BigInteger> tags = new ArrayList<>();
    private List<Long> tags = new ArrayList<>();

    public ActivityDTO() {
        //default constructor
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

    public PermissionsActivityTabDTO getPermissionsActivityTab() {
        return permissionsActivityTab;
    }


    public void setPermissionsActivityTab(PermissionsActivityTabDTO permissionsActivityTab) {
        this.permissionsActivityTab = permissionsActivityTab;
    }

    public List<BigInteger> getTags() {
        List<BigInteger> tags = new ArrayList<>();
        for (Long tag : this.tags) {
            tags.add(BigInteger.valueOf(tag));
        }
        return tags;
    }


    public void setTags(List<Long> tags) {

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


    public ActivityDTO(String name, String description, Long countryId, String categoryName, Long unitId, boolean isParentActivity) {
        this.name = name;
        this.description = description;
        this.countryId = countryId;
        this.categoryName = categoryName;
        this.unitId = unitId;
        this.isParentActivity = isParentActivity;
    }

    public ActivityDTO(BigInteger id, String name, String description, Long countryId, BigInteger categoryId, String categoryName, Long unitId, boolean isParentActivity,
                       GeneralActivityTabDTO generalActivityTab, List<Long> tags) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.countryId = countryId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.unitId = unitId;
        this.isParentActivity = isParentActivity;
        this.generalActivityTab = generalActivityTab;
        this.tags = tags;
    }

    public ActivityDTO(BigInteger id, String name,BigInteger parentId,PermissionsActivityTabDTO permissionsActivityTab) {

    }

    public ActivityDTO(BigInteger id, String name, BigInteger parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.permissionsActivityTab=permissionsActivityTab;
    }
}


