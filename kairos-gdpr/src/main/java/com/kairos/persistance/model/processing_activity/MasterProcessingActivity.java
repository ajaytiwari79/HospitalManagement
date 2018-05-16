package com.kairos.persistance.model.processing_activity;


import com.kairos.dto.OrganizationTypeAndServiceBasicDto;
import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Document(collection = "master_processing_activity")
public class MasterProcessingActivity extends MongoBaseEntity {

    @NotNullOrEmpty(message = "error.message.name.cannotbe.null.or.empty")
    private  String name;

    @NotNullOrEmpty(message = "error.message.name.cannotbe.null.or.empty")
    private String description;
    private List<OrganizationTypeAndServiceBasicDto> organisationType;

    private List <OrganizationTypeAndServiceBasicDto> organisationSubType;
    private List <OrganizationTypeAndServiceBasicDto>organisationService;
    private List <OrganizationTypeAndServiceBasicDto> organisationSubService;


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

    public List<OrganizationTypeAndServiceBasicDto> getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(List<OrganizationTypeAndServiceBasicDto> organisationType) {
        this.organisationType = organisationType;
    }

    public List<OrganizationTypeAndServiceBasicDto> getOrganisationSubType() {
        return organisationSubType;
    }

    public void setOrganisationSubType(List<OrganizationTypeAndServiceBasicDto> organisationSubType) {
        this.organisationSubType = organisationSubType;
    }

    public List<OrganizationTypeAndServiceBasicDto> getOrganisationService() {
        return organisationService;
    }

    public void setOrganisationService(List<OrganizationTypeAndServiceBasicDto> organisationService) {
        this.organisationService = organisationService;
    }

    public List<OrganizationTypeAndServiceBasicDto> getOrganisationSubService() {
        return organisationSubService;
    }

    public void setOrganisationSubService(List<OrganizationTypeAndServiceBasicDto> organisationSubService) {
        this.organisationSubService = organisationSubService;
    }

    public MasterProcessingActivity()
    {

    }
}
