package com.kairos.persistence.model.organization.services;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.UserMessagesConstants.ERROR_ORGANIZATIONSERVICE_NAME_NOTEMPTY;
import static com.kairos.persistence.model.constants.RelationshipConstants.ORGANIZATION_SUB_SERVICE;


/**
 * Created by oodles on 14/9/16.
 */

@NodeEntity
public class OrganizationService extends UserBaseEntity {
    @NotBlank(message = ERROR_ORGANIZATIONSERVICE_NAME_NOTEMPTY)
    private String name;

    private String description;


    @Relationship(type = ORGANIZATION_SUB_SERVICE)
    private List<OrganizationService> organizationSubService;

    private boolean isEnabled = true;

    private String kmdExternalId;

    private boolean imported = false;

    private boolean hasMapped = false;




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
