package com.kairos.persistence.model.user.office_esources_and_metadata;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.List;

/**
 * Created by @pankaj on 9/2/17.
 */
@NodeEntity
public class OfficeResourceTypeMetadata extends UserBaseEntity {
    private List<String> officeResource;
    private List<String> vehicleResource;

    public OfficeResourceTypeMetadata() {
    }

    public OfficeResourceTypeMetadata(List<String> officeResource, List<String> vehicleResource) {
        this.officeResource = officeResource;
        this.vehicleResource = vehicleResource;
    }

    public List<String> getOfficeResource() {
        return officeResource;
    }

    public void setOfficeResource(List<String> officeResource) {
        this.officeResource = officeResource;
    }

    public List<String> getVehicleResource() {
        return vehicleResource;
    }

    public void setVehicleResource(List<String> vehicleResource) {
        this.vehicleResource = vehicleResource;
    }
}
