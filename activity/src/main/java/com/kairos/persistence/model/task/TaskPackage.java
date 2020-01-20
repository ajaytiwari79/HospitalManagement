package com.kairos.persistence.model.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.MongoBaseEntity;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by neuron on 15/11/16.
 */
@Document(collection = "task_packages")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskPackage extends MongoBaseEntity {

    @NotNull(message = "Package name cannot be null") @NotEmpty(message = "Package name cannot be left blank")
    private String packageName;

    private long unitId;

    
    private List<String> taskDemandIds;
    private boolean isDeleted = false;



    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getUnitId() {
        return unitId;
    }

    public void setUnitId(long unitId) {
        this.unitId = unitId;
    }

    public List<String> getTaskDemandIds() {
        return taskDemandIds;
    }

    public void setTaskDemandIds(List<String> taskDemandIds) {
        this.taskDemandIds = taskDemandIds;
    }

   /* private long createdByOrganizationId;

    private List<Map<String,Object>> taskTypeDetail;

    public TaskPackage(String packageName,long createdByOrganizationId,List<Map<String,Object>> taskTypeDetail){
        this.packageName = packageName;
        this.createdByOrganizationId = createdByOrganizationId;
        this.taskTypeDetail = taskTypeDetail;
    }

    public String getPackageName() {
        return packageName;
    }

    public List<Map<String, Object>> getTaskTypeDetail() {
        return taskTypeDetail;
    }

    public long getCreatedByOrganizationId() {
        return createdByOrganizationId;
    }

    public TaskPackage(){}

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setCreatedByOrganizationId(long createdByOrganizationId) {
        this.createdByOrganizationId = createdByOrganizationId;
    }

    public void setTaskTypeDetail(List<Map<String, Object>> taskTypeDetail) {
        this.taskTypeDetail = taskTypeDetail;
    }*/

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
