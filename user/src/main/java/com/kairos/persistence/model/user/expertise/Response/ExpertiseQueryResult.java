package com.kairos.persistence.model.user.expertise.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.shift.BreakPaymentSetting;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.services.OrganizationService;
import com.kairos.persistence.model.organization.union.Location;
import com.kairos.persistence.model.organization.union.Sector;
import com.kairos.persistence.model.user.expertise.CareDays;
import com.kairos.persistence.model.pay_table.PayTable;
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
    private List<CareDays> seniorDays;
    private List<CareDays> childCareDays;
    private Level organizationLevel;
    private List<OrganizationService> organizationService;
    //TODO in current unwinded property cant be set to any nested domain to QueryResult DTO , We will change if in feature this will handle
    private Organization union;
    private PayTable payTable;
    private List<Map<String, Object>> seniorityLevels;
    private BreakPaymentSetting breakPaymentSetting;
    private Sector sector;
    private Map<String,Object> unionRepresentative;// in case of expertise at unit level only
    private Location unionLocation;// in case of expertise at unit level only

    public ExpertiseQueryResult() {
        //default  const
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

    public Level getOrganizationLevel() {
        return organizationLevel;
    }

    public void setOrganizationLevel(Level organizationLevel) {
        this.organizationLevel = organizationLevel;
    }

    public List<OrganizationService> getOrganizationService() {
        return organizationService;
    }

    public void setOrganizationService(List<OrganizationService> organizationService) {
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

    public List<CareDays> getSeniorDays() {
        return seniorDays;
    }

    public void setSeniorDays(List<CareDays> seniorDays) {
        this.seniorDays = seniorDays;
    }

    public List<CareDays> getChildCareDays() {
        return childCareDays;
    }

    public void setChildCareDays(List<CareDays> childCareDays) {
        this.childCareDays = childCareDays;
    }

    public BreakPaymentSetting getBreakPaymentSetting() {
        return breakPaymentSetting;
    }

    public void setBreakPaymentSetting(BreakPaymentSetting breakPaymentSetting) {
        this.breakPaymentSetting = breakPaymentSetting;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }

    public Map<String, Object> getUnionRepresentative() {
        return unionRepresentative;
    }

    public void setUnionRepresentative(Map<String, Object> unionRepresentative) {
        this.unionRepresentative = unionRepresentative;
    }

    public Location getUnionLocation() {
        return unionLocation;
    }

    public void setUnionLocation(Location unionLocation) {
        this.unionLocation = unionLocation;
    }
}
