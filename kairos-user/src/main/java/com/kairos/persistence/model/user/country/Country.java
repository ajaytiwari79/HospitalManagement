package com.kairos.persistence.model.user.country;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.OrganizationService;
import com.kairos.persistence.model.user.agreement.cta.RuleTemplate;
import com.kairos.persistence.model.user.agreement.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.model.user.resources.Vehicle;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotNull;
import java.util.*;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;
import static org.neo4j.ogm.annotation.Relationship.UNDIRECTED;


/**
 * Country Domain extending Base Entity
 * Country has relationship with CountryHolidayCalender and SkillCategory
 */
    /*
    * @modified by vipul
    * to add rule template relationship
    * 2 august 2017
    */
@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Country extends UserBaseEntity {

    @NotEmpty(message = "error.Country.name.notEmpty")
    @NotNull(message = "error.Country.name.notnull")
    private String name;

    private boolean isEnabled = true;

    @NotEmpty(message = "error.Country.code.notEmpty")
    @NotNull(message = "error.Country.code.notnull")
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Relationship(type = HAS_HOLIDAY)
    private List<CountryHolidayCalender> countryHolidayCalenderList;

    @Relationship(type = HAS_RULE_TEMPLATE_CATEGORY, direction = UNDIRECTED)
    private List<RuleTemplateCategory> ruleTemplateCategories = new ArrayList<>();

    @Relationship(type = HAS_RULE_TEMPLATE)
    private List<RuleTemplate> WTABaseRuleTemplate;

    @JsonIgnore
    @Relationship(type = HAS_ORGANIZATION_SERVICES)
    private List<OrganizationService> organizationServices;

    @Relationship(type = HAS_LEVEL)
    private List<Level> levels;

    @Relationship(type = HAS_RELATION_TYPES)
    private List<RelationType> relationTypes;

    @Relationship(type = HAS_RESOURCES)
    private List<Vehicle> resources;

    @Relationship(type = HAS_EMPLOYMENT_TYPE)
    private List<EmploymentType> employmentTypeList;

    public Country() {
    }

    public Country(String name, List<CountryHolidayCalender> countryHolidayCalenderList) {
        this.countryHolidayCalenderList = countryHolidayCalenderList;
        this.name = name;
    }


    public Country(String name) {
        this.name = name;
    }

    public List<RuleTemplate> getWTABaseRuleTemplate() {
        return WTABaseRuleTemplate;
    }

    public void setWTABaseRuleTemplate(List<RuleTemplate> WTABaseRuleTemplate) {
        this.WTABaseRuleTemplate = WTABaseRuleTemplate;
    }

    public String getGoogleCalendarCode() {
        return googleCalendarCode;
    }

    public void setGoogleCalendarCode(String googleCalendarCode) {
        this.googleCalendarCode = googleCalendarCode;
    }

    private String googleCalendarCode;

    public List<CountryHolidayCalender> getCountryHolidayCalenderList() {
        return Optional.ofNullable(countryHolidayCalenderList).orElse(new ArrayList<>());

    }

    public void setCountryHolidayCalenderList(List<CountryHolidayCalender> countryHolidayCalenderList) {
        this.countryHolidayCalenderList = countryHolidayCalenderList;
    }

//    public List<OrganizationType> getOrganizationTypes() {
//        return organizationTypeList;
//    }
//
//    public void setOrganizationTypes(List<OrganizationType> organizationTypeList) {
//        this.organizationTypeList = organizationTypeList;
//    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<OrganizationService> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(List<OrganizationService> organizationServices) {
        this.organizationServices = organizationServices;
    }

    public void setEmploymentTypeList(List<EmploymentType> employmentTypeList) {
        this.employmentTypeList = employmentTypeList;
    }

    public void addEmploymentType(EmploymentType employmentType) {
        List<EmploymentType> employmentTypeList = Optional.ofNullable(this.employmentTypeList).orElse(new ArrayList<>());
        employmentTypeList.add(employmentType);
        this.employmentTypeList = employmentTypeList;
    }

    public List<EmploymentType> getEmploymentTypeList() {
        return employmentTypeList;
    }

    public List<RuleTemplateCategory> getRuleTemplateCategories() {
        return Optional.ofNullable(ruleTemplateCategories).orElse(new ArrayList<>());
    }


    public void addLevel(Level level) {
        List<Level> levels = Optional.ofNullable(this.levels).orElse(new ArrayList<>());
        levels.add(level);
        this.levels = levels;
    }

    public void addResources(Vehicle vehicle) {
        List<Vehicle> resourceList = Optional.ofNullable(this.resources).orElse(new ArrayList<>());
        resourceList.add(vehicle);
        this.resources = resourceList;
    }


    public List<RelationType> getRelationTypes() {
        return relationTypes;
    }

    public void setRelationTypes(List<RelationType> relationTypes) {
        this.relationTypes = relationTypes;
    }

    public void setRuleTemplateCategories(List<RuleTemplateCategory> ruleTemplateCategories) {
        this.ruleTemplateCategories = ruleTemplateCategories;
    }

    public void addRuleTemplateCategory(RuleTemplateCategory ruleTemplateCategory) {
        if (ruleTemplateCategory == null)
            throw new NullPointerException("Can't add null ruleTemplateCategory");
        if (ruleTemplateCategory.getCountry() != null)
            throw new IllegalStateException("country is already assigned to ruleTemplateCategory");
        getRuleTemplateCategories().add(ruleTemplateCategory);
        ruleTemplateCategory.setCountry(this);
    }

    public void removeRuleTemplateCategory(RuleTemplateCategory ruleTemplateCategory) {
        if (ruleTemplateCategory == null)
            getRuleTemplateCategories().remove(ruleTemplateCategory);
        ruleTemplateCategory.setCountry(null);

    }

    public Map<String, Object> retrieveGeneralDetails() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.id);
        map.put("name", this.name);
        return map;
    }

    public Map<String, Object> retrieveDetails() {
        Map<String, Object> map = new HashMap();
        map.put("id", this.id);
        map.put("name", this.name);
        map.put("code", this.code);
        map.put("lastModificationDate", this.getLastModificationDate());
        map.put("creationDate", this.getCreationDate());
        map.put("googleCalendarCode", this.getGoogleCalendarCode());
        return map;
    }

    @Override
    public String toString() {
        return "{Country={" +
                "name='" + name + '\'' +
                ", isEnabled=" + isEnabled +
                ", code='" + code + '\'' +
                '}' +
                '}';
    }


}
