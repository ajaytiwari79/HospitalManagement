package com.kairos.persistance.model.organization;


import com.kairos.persistance.model.common.MongoBaseEntity;
import org.hibernate.validator.constraints.NotEmpty;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by oodles on 14/9/16.
 */

@Document(collection = "organization_service")
public class OrganizationService extends MongoBaseEntity {


    @NotEmpty(message = "error.OrganizationService.name.notEmpty")
    @NotNull(message = "error.OrganizationService.name.notnull")
    private String name;

   // @NotEmpty(message = "error.OrganizationService.description.notEmpty") @NotNull(message = "error.OrganizationService.description.notnull")
    private String description;

    private List<OrganizationService> organizationSubService;

    private boolean isEnabled = true;

    private String kmdExternalId;

    private boolean imported = false;

    private boolean hasMapped = false;


    public OrganizationService(String name, List<OrganizationService> organizationSubServicesList) {
        this.name = name;
        this.organizationSubService = organizationSubServicesList;
    }

    public OrganizationService(String name) {
        this.name = name;
    }


    public OrganizationService() {
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<OrganizationService> getOrganizationSubService() {
        return organizationSubService;
    }

    public void setOrganizationSubService(List<OrganizationService> organizationSubService) {
        this.organizationSubService = organizationSubService;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> retrieveDetails() {
        Map<String,Object> map = new HashMap<>();
        map.put("id",this.id);
        map.put("name",this.name);
        map.put("description",this.description);
        return map;
    }

    public String getKmdExternalId() {
        return kmdExternalId;
    }

    public void setKmdExternalId(String kmdExternalId) {
        this.kmdExternalId = kmdExternalId;
    }

    public boolean isImported() {
        return imported;
    }

    public void setImported(boolean imported) {
        this.imported = imported;
    }

    public boolean isHasMapped() {
        return hasMapped;
    }

    public void setHasMapped(boolean hasMapped) {
        this.hasMapped = hasMapped;
    }
}
