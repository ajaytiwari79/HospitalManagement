package com.kairos.persistence.model.organization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.country.Country;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by oodles on 14/9/16.
 */

@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationType extends UserBaseEntity {

    private boolean isEnable = true;
    @NotEmpty(message = "error.OrganizationType.name.notEmpty") @NotNull(message = "error.OrganizationType.name.notnull")
    private String name;

//    @NotEmpty(message = "error.OrganizationType.description.notEmpty") @NotNull(message = "error.OrganizationType.description.notnull")
    private String description;


    @Relationship(type = HAS_SUB_TYPE)
    private List<OrganizationType> organizationTypeList;

    @Relationship(type = ORGANIZATION_TYPE_HAS_SERVICES)
    List<OrganizationService> organizationServiceList;

    @Relationship(type = BELONGS_TO)
    private Country country;


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

}
