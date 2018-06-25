package com.kairos.persistance.model.filter;


import com.kairos.dto.master_data.ModuleIdDto;
import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.enums.FilterType;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Document(collection = "filterGroup")
public class FilterGroup extends MongoBaseEntity {


    @NotNull
    @NotEmpty
    private List<ModuleIdDto> accessModule;

    @NotNull
    @NotEmpty
    private List<FilterType> filterTypes;

    private Long countryId;


    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

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
    public FilterGroup(List<ModuleIdDto> accessModule,List<FilterType> filterTypes,Long countryId) {

        this.filterTypes=filterTypes;
        this.accessModule=accessModule;
        this.countryId=countryId;
    }


}
