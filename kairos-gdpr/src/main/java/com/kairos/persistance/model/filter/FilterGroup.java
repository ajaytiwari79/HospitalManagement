package com.kairos.persistance.model.filter;


import com.kairos.dto.ModuleIdDto;
import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.persistance.model.enums.FilterType;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.List;

@Document(collection = "filterGroup")
public class FilterGroup extends MongoBaseEntity {

    @NotNull
    private List<ModuleIdDto> accessModule;

    @NotNull
    private List<FilterType> filterTypes;

    public List<ModuleIdDto> getAccessModule() {
        return accessModule;
    }

    public void setAccessModule(List<ModuleIdDto> accessModule) {
        this.accessModule = accessModule;
    }

    public List<FilterType> getFilterTypes() {
        return filterTypes;
    }

    public void setFilterTypes(List<FilterType> filterTypes) {
        this.filterTypes = filterTypes;
    }

    public FilterGroup() {

    }
}
