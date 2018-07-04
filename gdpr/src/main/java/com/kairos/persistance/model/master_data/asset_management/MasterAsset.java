package com.kairos.persistance.model.master_data.asset_management;


import com.kairos.dto.OrganizationTypeAndServiceBasicDTO;
import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.List;

@Document(collection = "master_asset")
public class MasterAsset extends MongoBaseEntity {


    @NotNullOrEmpty(message = "error.message.name.cannotbe.null.or.empty")
    private  String name;

    @NotNullOrEmpty(message = "error.message.name.cannotbe.null.or.empty")
    private String description;

    @NotNull(message = "error.message.cannot.be.null")
    private List<OrganizationTypeAndServiceBasicDTO> organizationTypes;

    @NotNull
    private List <OrganizationTypeAndServiceBasicDTO> organizationSubTypes;
    @NotNull
    private List <OrganizationTypeAndServiceBasicDTO> organizationServices;
    @NotNull
    private List <OrganizationTypeAndServiceBasicDTO> organizationSubServices;

    private Long countryId;

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
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

    public List<OrganizationTypeAndServiceBasicDTO> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<OrganizationTypeAndServiceBasicDTO> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<OrganizationTypeAndServiceBasicDTO> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<OrganizationTypeAndServiceBasicDTO> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public List<OrganizationTypeAndServiceBasicDTO> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(List<OrganizationTypeAndServiceBasicDTO> organizationServices) {
        this.organizationServices = organizationServices;
    }

    public List<OrganizationTypeAndServiceBasicDTO> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(List<OrganizationTypeAndServiceBasicDTO> organizationSubServices) {
        this.organizationSubServices = organizationSubServices;
    }
}
