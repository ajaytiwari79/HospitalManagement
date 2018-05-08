package com.kairos.persistance.model.user.country;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.persistance.model.organization.OrganizationService;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;



@Document(collection = "country")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Country extends MongoBaseEntity {



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

    @JsonIgnore
    private List<OrganizationService> organizationServices;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }


    public List<OrganizationService> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(List<OrganizationService> organizationServices) {
        this.organizationServices = organizationServices;
    }



    public Country() {
    }


    public Country(String name) {
        this.name = name;
    }



    public  Map<String, Object> retrieveGeneralDetails() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.id);
        map.put("name", this.name);
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
