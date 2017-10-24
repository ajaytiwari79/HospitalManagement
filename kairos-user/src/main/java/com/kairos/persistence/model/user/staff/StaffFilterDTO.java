package com.kairos.persistence.model.user.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by Jasgeet on 13/10/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffFilterDTO {
   private String moduleId;
   private String filterJson;
    private long id;
    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getFilterJson() {
        return filterJson;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFilterJson(String filterJson) {
        this.filterJson = filterJson;
    }

    public StaffFilterDTO(String moduleId, String filterJson, String name) {
        this.moduleId = moduleId;
        this.filterJson = filterJson;
        this.name = name;
    }
}

