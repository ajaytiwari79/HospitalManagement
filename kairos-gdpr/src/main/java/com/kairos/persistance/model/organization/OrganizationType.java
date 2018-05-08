package com.kairos.persistance.model.organization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;


import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.persistance.model.user.country.Country;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.*;



/**
 * Created by oodles on 14/9/16.
 */


@Document(collection = "organization_type")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganizationType  extends MongoBaseEntity {



    private boolean isEnable = true;
    @NotEmpty(message = "error.OrganizationType.name.notEmpty")
    @NotNull(message = "error.OrganizationType.name.notnull")
    private String name;

    //    @NotEmpty(message = "error.OrganizationTypeMongoRepository.description.notEmpty") @NotNull(message = "error.OrganizationTypeMongoRepository.description.notnull")
    private String description;


    private List<OrganizationType> organizationTypeList;

    private List<OrganizationService> organizationServiceList;

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



    @Override
    public String toString() {
        return "OrganizationTypeMongoRepository{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }



}
