package com.kairos.persistance.model.asset;


import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.persistance.model.dto.CustomOrganizationTypeAndServiceDto;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Document(collection = "global_asset")
public class MasterAsset extends MongoBaseEntity {


    @NotNullOrEmpty(message = "error.message.name.cannotbe.null.or.empty")
    private  String name;

    @NotNullOrEmpty(message = "error.message.name.cannotbe.null.or.empty")
    private String description;
    private List<CustomOrganizationTypeAndServiceDto> organisationType;

    private List <CustomOrganizationTypeAndServiceDto> organisationSubType;
    private List <CustomOrganizationTypeAndServiceDto>organisationService;
    private List <CustomOrganizationTypeAndServiceDto> organisationSubService;

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

    public List<CustomOrganizationTypeAndServiceDto> getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(List<CustomOrganizationTypeAndServiceDto> organisationType) {
        this.organisationType = organisationType;
    }

    public List<CustomOrganizationTypeAndServiceDto> getOrganisationSubType() {
        return organisationSubType;
    }

    public void setOrganisationSubType(List<CustomOrganizationTypeAndServiceDto> organisationSubType) {
        this.organisationSubType = organisationSubType;
    }

    public List<CustomOrganizationTypeAndServiceDto> getOrganisationService() {
        return organisationService;
    }

    public void setOrganisationService(List<CustomOrganizationTypeAndServiceDto> organisationService) {
        this.organisationService = organisationService;
    }

    public List<CustomOrganizationTypeAndServiceDto> getOrganisationSubService() {
        return organisationSubService;
    }

    public void setOrganisationSubService(List<CustomOrganizationTypeAndServiceDto> organisationSubService) {
        this.organisationSubService = organisationSubService;
    }
}
