package com.kairos.persistance.model.processing_activity;


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
    private List<Long> organisationType;

    private List <Long> organisationSubType;
    private List <Long>organisationService;
    private List <Long> organisationSubService;

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

    public List<Long> getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(List<Long> organisationType) {
        this.organisationType = organisationType;
    }

    public List<Long> getOrganisationSubType() {
        return organisationSubType;
    }

    public void setOrganisationSubType(List<Long> organisationSubType) {
        this.organisationSubType = organisationSubType;
    }

    public List<Long> getOrganisationService() {
        return organisationService;
    }

    public void setOrganisationService(List<Long> organisationService) {
        this.organisationService = organisationService;
    }

    public List<Long> getOrganisationSubService() {
        return organisationSubService;
    }

    public void setOrganisationSubService(List<Long> organisationSubService) {
        this.organisationSubService = organisationSubService;
    }
}
