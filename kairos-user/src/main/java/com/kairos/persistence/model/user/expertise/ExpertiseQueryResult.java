package com.kairos.persistence.model.user.expertise;

import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationService;
import com.kairos.persistence.model.organization.union.UnionResponseDTO;
import com.kairos.persistence.model.user.pay_table.PayTable;
import com.kairos.response.dto.web.experties.PaidOutFrequencyEnum;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.Map;

/**
 * Created by vipul on 28/3/18.
 */
@QueryResult
public class ExpertiseQueryResult {
    private String name;
    private String description;
    private Long startDateMillis;
    private Long endDateMillis;
    private Integer fullTimeWeeklyMinutes;
    private Integer numberOfWorkingDaysInWeek;
    private Long id;
    private Boolean published;
    private PaidOutFrequencyEnum paidOutFrequency;


    private Map<String, Object> organizationLevel;
    private Map<String, Object> organizationService;
    //TODO in current unwinded property cant be set to any nested domain to QueryResult DTO , We will change if in feature this will handle
    private Map<String, Object> union;
    private Map<String, Object> payTable;


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

    public Map<String, Object> getOrganizationLevel() {
        return organizationLevel;
    }

    public void setOrganizationLevel(Map<String, Object> organizationLevel) {
        this.organizationLevel = organizationLevel;
    }

    public Map<String, Object> getOrganizationService() {
        return organizationService;
    }

    public void setOrganizationService(Map<String, Object> organizationService) {
        this.organizationService = organizationService;
    }

    public Map<String, Object> getUnion() {
        return union;
    }

    public void setUnion(Map<String, Object> union) {
        this.union = union;
    }

    public Map<String, Object> getPayTable() {
        return payTable;
    }

    public void setPayTable(Map<String, Object> payTable) {
        this.payTable = payTable;
    }

    public ExpertiseQueryResult() {
        //default  const
    }
}
