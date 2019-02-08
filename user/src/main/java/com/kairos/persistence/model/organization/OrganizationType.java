package com.kairos.persistence.model.organization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.organization.services.OrganizationService;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.*;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by oodles on 14/9/16.
 */


@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganizationType extends UserBaseEntity {

    private boolean isEnable = true;
    @NotBlank(message = "error.OrganizationType.name.notEmpty")
    private String name;
    private String description;
    @Relationship(type = HAS_SUB_TYPE)
    private List<OrganizationType> organizationTypeList;
    @Relationship(type = ORGANIZATION_TYPE_HAS_SERVICES)
    List<OrganizationService> organizationServiceList;
    @Relationship(type = BELONGS_TO)
    private Country country;
    @Relationship(type = HAS_LEVEL)
    private List<Level> levels;

    public OrganizationType(String name, Country country, List<Level> levels) {
        this.name = name;
        this.country = country;
        this.levels = levels;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public OrganizationType(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {

        return description;
    }

    public List<OrganizationService> getOrganizationServiceList() {
        return organizationServiceList;
    }

    public void setOrganizationServiceList(List<OrganizationService> organizationServiceList) {
        this.organizationServiceList = organizationServiceList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<OrganizationType> getOrganizationTypeList() {
        return organizationTypeList;
    }

    public void setOrganizationTypeList(List<OrganizationType> organizationTypeList) {
        this.organizationTypeList = organizationTypeList;
    }

    public OrganizationType(String name, List<OrganizationService> organizationServiceList) {
        this.name = name;
        this.organizationServiceList = organizationServiceList;
    }

    public List<Level> getLevels() {
        return Optional.ofNullable(levels).orElse(new ArrayList<>());
    }

    public void setLevels(List<Level> levels) {
        this.levels = levels;
    }

    public OrganizationType(){}

    public enum OrganizationTypeEnum {
        PUBLIC("Public"),PRIVATE("Private"),NGO("Ngo");
        public String value;

        OrganizationTypeEnum(String value) {
            this.value = value;
        }

        public OrganizationTypeEnum getByValue(String value){
            for(OrganizationTypeEnum organizationTypeEnum : OrganizationTypeEnum.values()){
                if(organizationTypeEnum.value.equals(value)){
                    return organizationTypeEnum;
                }
            }
            return null;
        }

    }

    public Map<String, Object> retrieveDetails() {
        Map<String, Object> map = new HashMap();
        map.put("id",this.id);
        map.put("name",this.name);
        map.put("description",this.description);
        map.put("lastModificationDate",this.getLastModificationDate());
        map.put("creationDate",this.getCreationDate());
        return map;
    }

    public OrganizationType(Long id,@NotBlank(message = "error.OrganizationType.name.notEmpty")String name, String description) {
        this.name = name;
        this.id=id;
        this.description = description;
    }

    public OrganizationType basicDetails(){
        OrganizationType organizationType = new OrganizationType(this.id, this.name, this.description);
        return organizationType;
    }
    @Override
    public String toString() {
        return "OrganizationType{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

}
