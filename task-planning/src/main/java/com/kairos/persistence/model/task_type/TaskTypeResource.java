package com.kairos.persistence.model.task_type;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prerna on 7/12/17.
 */
public class TaskTypeResource {
    private Long resourceId;
    private List<Long> features = new ArrayList<Long>();
    private List<Long> equipments = new ArrayList<Long>();

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public List<Long> getFeatures() {
        return features;
    }

    public void setFeatures(List<Long> features) {
        this.features = features;
    }

    public List<Long> getEquipments() {
        return equipments;
    }

    public void setEquipments(List<Long> equipments) {
        this.equipments = equipments;
    }
}
