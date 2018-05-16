package com.kairos.persistance.model.clause;


import com.kairos.dto.OrganizationTypeAndServiceBasicDto;
import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.persistance.model.organization.OrganizationService;
import com.kairos.persistance.model.organization.OrganizationType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


@Document(collection = "clause")
public class Clause extends MongoBaseEntity {

    @NotEmpty(message = "error.clause.title.cannotbe.empty")
    @NotNull(message = "error.clause.title.cannotbe.null")
    private String title;

    private List<String> tags = new ArrayList<>();

    @NotEmpty(message = "error.clause.title.cannotbe.empty")
    @NotNull(message = "error.clause.title.cannotbe.null")
    private String description;


    private List<OrganizationTypeAndServiceBasicDto> organisationType;

    private List <OrganizationTypeAndServiceBasicDto> organisationSubType;
    private List <OrganizationTypeAndServiceBasicDto>organisationService;
    private List <OrganizationTypeAndServiceBasicDto> organisationSubService;


    private List<AccountType> accountTypes;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

     public Clause() {

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

    public List<AccountType> getAccountTypes() {
        return accountTypes;
    }

    public void setAccountTypes(List<AccountType> accountTypes) {
        this.accountTypes = accountTypes;
    }
}
