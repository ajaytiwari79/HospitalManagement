package com.kairos.persistance.model.clause;


import com.kairos.dto.OrganizationTypeAndServiceBasicDTO;
import com.kairos.persistance.model.account_type.AccountType;
import com.kairos.persistance.model.clause_tag.ClauseTag;
import com.kairos.persistance.model.common.JaversBaseEntity;
import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


@Document(collection = "clause")
public class Clause extends JaversBaseEntity {

    @NotNullOrEmpty
    private String title;
    @NotNull
    private List<ClauseTag> tags = new ArrayList<>();
    @NotNull
    private String description;

    private List<OrganizationTypeAndServiceBasicDTO> organizationTypes;
    private List<OrganizationTypeAndServiceBasicDTO> organizationSubTypes;
    private List<OrganizationTypeAndServiceBasicDTO> organizationServices;
    private List<OrganizationTypeAndServiceBasicDTO> organizationSubServices;
    private List<AccountType> accountTypes;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ClauseTag> getTags() {
        return tags;
    }

    public void setTags(List<ClauseTag> tags) {
        this.tags = tags;
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

    public List<AccountType> getAccountTypes() {
        return accountTypes;
    }

    public void setAccountTypes(List<AccountType> accountTypes) {
        this.accountTypes = accountTypes;
    }

    public Clause(Long countryId, String title, String description) {
        this.countryId = countryId;
        this.title = title;
        this.description = description;
    }

    public Clause() {
    }
}
