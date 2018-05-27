package com.kairos.persistance.model.filter;


import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.persistance.model.enums.FilterType;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "filterGroup")
public class FilterGroup extends MongoBaseEntity {


    private String name;
    private String moduleId;
    private Boolean isModule;
    private Boolean active;
    private List<FilterType> filterTypes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public Boolean getModule() {
        return isModule;
    }

    public void setModule(Boolean module) {
        isModule = module;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<FilterType> getFilterTypes() {
        return filterTypes;
    }

    public void setFilterTypes(List<FilterType> filterTypes) {
        this.filterTypes = filterTypes;
    }

    public FilterGroup()
    {

    }
}
