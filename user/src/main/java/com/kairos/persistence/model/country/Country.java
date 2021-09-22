package com.kairos.persistence.model.country;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.default_data.RelationType;
import com.kairos.persistence.model.country.employment_type.EmploymentType;
import com.kairos.persistence.model.country.equipment.Equipment;
import com.kairos.persistence.model.country.feature.Feature;
import com.kairos.persistence.model.country.tag.Tag;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.services.OrganizationService;
import com.kairos.persistence.model.user.resources.Vehicle;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


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
@Getter
@Setter
public class Country extends UserBaseEntity {

    private static final long serialVersionUID = -8239277648666031215L;
    @NotBlank(message = "error.Country.name.notEmpty")
    private String name;

    private boolean isEnabled = true;

    @NotBlank(message = "error.Country.code.notEmpty")
    private String code;

    @Relationship(type = COUNTRY_HAS_TAG)
    private List<Tag> tags;

    @Relationship(type = COUNTRY_HAS_FEATURE )
    private List<Feature> features;

    @Relationship(type = COUNTRY_HAS_EQUIPMENT )
    private List<Equipment> equipments;
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

    private Set<BigInteger> payRollTypeIds;

    private String googleCalendarCode;


    public void addEmploymentType(EmploymentType employmentType) {
        List<EmploymentType> employmentTypeList = Optional.ofNullable(this.employmentTypeList).orElse(new ArrayList<>());
        employmentTypeList.add(employmentType);
        this.employmentTypeList = employmentTypeList;
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
