package com.kairos.persistence.model.user.office_esources_and_metadata;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;


/**
 * Created by @pankaj on 9/2/17.
 */
@NodeEntity
public class OfficeResources extends UserBaseEntity {

    private String name;
    private String resourceType;

    public OfficeResources(String name, String resourceType) {
        this.name = name;
        this.resourceType = resourceType;
    }

    public OfficeResources() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
}
