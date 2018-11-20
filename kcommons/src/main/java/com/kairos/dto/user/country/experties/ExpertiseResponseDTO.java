package com.kairos.dto.user.country.experties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.user.organization.union.SectorDTO;
import com.kairos.dto.user.organization.union.UnionIDNameDTO;
import com.kairos.enums.shift.BreakPaymentSetting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by vipul on 29/3/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExpertiseResponseDTO {
    private Long id;
    private Long parentId;
    private String name;
    private String description;
    //@DateLong
    private Date startDateMillis;
    //@DateLong
    private Date endDateMillis;
    private Long organizationLevelId;
    private Set<Long> organizationServiceIds;
    private UnionIDNameDTO union;
    private int fullTimeWeeklyMinutes;
    private Integer numberOfWorkingDaysInWeek;

    private List<SeniorityLevelDTO> seniorityLevels = new ArrayList<>();
    private List<Long> tags;
    private Boolean published;
    private Boolean editable;
    private BreakPaymentSetting breakPaymentSetting;
    private SectorDTO sector;

    public ExpertiseResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
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

    public Date getStartDateMillis() {
        return startDateMillis;
    }

    public void setStartDateMillis(Date startDateMillis) {
        this.startDateMillis = startDateMillis;
    }

    public Date getEndDateMillis() {
        return endDateMillis;
    }

    public void setEndDateMillis(Date endDateMillis) {
        this.endDateMillis = endDateMillis;
    }

    public Long getOrganizationLevelId() {
        return organizationLevelId;
    }

    public void setOrganizationLevelId(Long organizationLevelId) {
        this.organizationLevelId = organizationLevelId;
    }

    public Set<Long> getOrganizationServiceIds() {
        return organizationServiceIds;
    }

    public void setOrganizationServiceIds(Set<Long> organizationServiceIds) {
        this.organizationServiceIds = organizationServiceIds;
    }



    public int getFullTimeWeeklyMinutes() {
        return fullTimeWeeklyMinutes;
    }

    public void setFullTimeWeeklyMinutes(int fullTimeWeeklyMinutes) {
        this.fullTimeWeeklyMinutes = fullTimeWeeklyMinutes;
    }

    public Integer getNumberOfWorkingDaysInWeek() {
        return numberOfWorkingDaysInWeek;
    }

    public void setNumberOfWorkingDaysInWeek(Integer numberOfWorkingDaysInWeek) {
        this.numberOfWorkingDaysInWeek = numberOfWorkingDaysInWeek;
    }


    public List<SeniorityLevelDTO> getSeniorityLevels() {
        return seniorityLevels;
    }

    public void setSeniorityLevels(List<SeniorityLevelDTO> seniorityLevels) {
        this.seniorityLevels = seniorityLevels;
    }

    public List<Long> getTags() {
        return tags;
    }

    public void setTags(List<Long> tags) {
        this.tags = tags;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public BreakPaymentSetting getBreakPaymentSetting() {
        return breakPaymentSetting;
    }

    public void setBreakPaymentSetting(BreakPaymentSetting breakPaymentSetting) {
        this.breakPaymentSetting = breakPaymentSetting;
    }

    public SectorDTO getSector() {
        return sector;
    }

    public void setSector(SectorDTO sector) {
        this.sector = sector;
    }

    public UnionIDNameDTO getUnion() {
        return union;
    }

    public void setUnion(UnionIDNameDTO union) {
        this.union = union;
    }
}
