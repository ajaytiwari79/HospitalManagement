package com.kairos.persistence.model.user.expertise;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationService;
import com.kairos.persistence.model.user.pay_table.PayTable;
import com.kairos.response.dto.web.experties.PaidOutFrequencyEnum;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Map;

/**
 * Created by vipul on 28/3/18.
 */
@QueryResult
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpertiseQueryResult {
    private String name;
    private String description;
    private Long startDateMillis;
    private Long endDateMillis;
    private Integer fullTimeWeeklyMinutes;
    private Integer numberOfWorkingDaysInWeek;
    private Long id;
    private Boolean published;
    private Boolean history;
    private PaidOutFrequencyEnum paidOutFrequency;
    private List<SeniorDays> seniorDays;
    private List<ChildCareDays> childCareDays;


    private Level organizationLevel;
    private OrganizationService organizationService;
    //TODO in current unwinded property cant be set to any nested domain to QueryResult DTO , We will change if in feature this will handle
    private Organization union;
    private PayTable payTable;
    private List<Map<String, Object>> seniorityLevels;

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

    public Long getStartDateMillis() {
        return startDateMillis;
    }

    public void setStartDateMillis(Long startDateMillis) {
        this.startDateMillis = startDateMillis;
    }

    public Long getEndDateMillis() {
        return endDateMillis;
    }

    public void setEndDateMillis(Long endDateMillis) {
        this.endDateMillis = endDateMillis;
    }

    public Integer getFullTimeWeeklyMinutes() {
        return fullTimeWeeklyMinutes;
    }

    public void setFullTimeWeeklyMinutes(Integer fullTimeWeeklyMinutes) {
        this.fullTimeWeeklyMinutes = fullTimeWeeklyMinutes;
    }

    public Integer getNumberOfWorkingDaysInWeek() {
        return numberOfWorkingDaysInWeek;
    }

    public void setNumberOfWorkingDaysInWeek(Integer numberOfWorkingDaysInWeek) {
        this.numberOfWorkingDaysInWeek = numberOfWorkingDaysInWeek;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public PaidOutFrequencyEnum getPaidOutFrequency() {
        return paidOutFrequency;
    }

    public void setPaidOutFrequency(PaidOutFrequencyEnum paidOutFrequency) {
        this.paidOutFrequency = paidOutFrequency;
    }

    public Level getOrganizationLevel() {
        return organizationLevel;
    }

    public void setOrganizationLevel(Level organizationLevel) {
        this.organizationLevel = organizationLevel;
    }

    public OrganizationService getOrganizationService() {
        return organizationService;
    }

    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    public Organization getUnion() {
        return union;
    }

    public void setUnion(Organization union) {
        this.union = union;
    }

    public PayTable getPayTable() {
        return payTable;
    }

    public void setPayTable(PayTable payTable) {
        this.payTable = payTable;
    }

    public List<Map<String, Object>> getSeniorityLevels() {
        return seniorityLevels;
    }

    public void setSeniorityLevels(List<Map<String, Object>> seniorityLevels) {
        this.seniorityLevels = seniorityLevels;
    }

    public Boolean getHistory() {
        return history;
    }

    public void setHistory(Boolean history) {
        this.history = history;
    }

    public ExpertiseQueryResult() {
        //default  const
    }

    public List<SeniorDays> getSeniorDays() {
        return seniorDays;
    }

    public void setSeniorDays(List<SeniorDays> seniorDays) {
        this.seniorDays = seniorDays;
    }

    public List<ChildCareDays> getChildCareDays() {
        return childCareDays;
    }

    public void setChildCareDays(List<ChildCareDays> childCareDays) {
        this.childCareDays = childCareDays;
    }
}
