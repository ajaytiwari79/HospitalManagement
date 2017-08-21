package com.kairos.persistence.model.user.country;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.OrganizationService;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Country Domain extending Base Entity
 * Country has relationship with CountryHolidayCalender and SkillCategory
 */
@NodeEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Country extends UserBaseEntity {

    @NotEmpty(message = "error.Country.name.notEmpty") @NotNull(message = "error.Country.name.notnull")
    private String name;

    private boolean isEnabled = true;

    @NotEmpty(message = "error.Country.code.notEmpty") @NotNull(message = "error.Country.code.notnull")
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Relationship(type = HAS_HOLIDAY)
    private List< CountryHolidayCalender> countryHolidayCalenderList;

    @JsonIgnore
    @Relationship(type = HAS_ORGANIZATION_SERVICES)
    private List<OrganizationService> organizationServices;

    public Country() {
    }

    public Country(String name, List<CountryHolidayCalender> countryHolidayCalenderList) {
        this.countryHolidayCalenderList = countryHolidayCalenderList;
        this.name = name;
    }


    public Country(String name) {
        this.name = name;
    }

    public Map<String, Object> retrieveGeneralDetails() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.id);
        map.put("name", this.name);
        return map;
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

//    public List<OrganizationType> getOrganizationTypeList() {
//        return organizationTypeList;
//    }
//
//    public void setOrganizationTypeList(List<OrganizationType> organizationTypeList) {
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

    public enum Designation {
        HOME_CARE, NURSING_HOME, HOSPITAL
    }

    public Map<String, Object> retrieveDetails() {
        Map<String, Object> map = new HashMap();
        map.put("id",this.id);
        map.put("name",this.name);
        map.put("code",this.code);
        map.put("lastModificationDate",this.getLastModificationDate());
        map.put("creationDate",this.getCreationDate());
        return map;
    }

    @Override
    public String toString() {
        return "{Country={" +
                "name='" + name + '\'' +
                ", isEnabled=" + isEnabled +
                ", code='" + code + '\'' +
                '}'+
                '}';
    }
}
