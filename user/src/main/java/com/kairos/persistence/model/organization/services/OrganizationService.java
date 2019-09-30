package com.kairos.persistence.model.organization.services;

import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.Setter;
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
@Getter
@Setter
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

    public Map<String, Object> retrieveDetails() {
        Map<String,Object> map = new HashMap<>(3);
        map.put("id",this.id);
        map.put("name",this.name);
        map.put("description",this.description);
        return map;
    }


}
