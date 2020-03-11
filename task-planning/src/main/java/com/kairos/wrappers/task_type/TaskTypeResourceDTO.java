package com.kairos.wrappers.task_type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.task_type.TaskTypeResource;

import java.util.List;

/**
 * Created by Jasgeet on 18/9/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskTypeResourceDTO {
    private boolean vehicleRequired;
//    private List<Long> resources;
    private List<TaskTypeResource> resources;

    public boolean isVehicleRequired() {
        return vehicleRequired;
    }

    public void setVehicleRequired(boolean vehicleRequired) {
        this.vehicleRequired = vehicleRequired;
    }

    public List<TaskTypeResource> getResources() {
        return resources;
    }

    public void setResources(List<TaskTypeResource> resources) {
        this.resources = resources;
    }

}
