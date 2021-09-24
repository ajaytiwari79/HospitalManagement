package com.kairos.persistence.model.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.filter.FilterGroup;
import com.kairos.persistence.model.user.filter.FilterSelection;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.FILTER_DETAIL;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_FILTER_GROUP;

/**
 * Created by Jasgeet on 13/10/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NodeEntity
public class StaffFavouriteFilter extends UserBaseEntity {

    @Relationship(type = HAS_FILTER_GROUP)
    private FilterGroup filterGroup;

    @Relationship(type = FILTER_DETAIL)
    private List<FilterSelection> filtersData;

    private String name;

    public StaffFavouriteFilter(){
        // default constructor
    }

    public StaffFavouriteFilter(String name, List<FilterSelection> filtersData, FilterGroup filterGroup){
        this.name = name;
        this.filtersData = filtersData;
        this.filterGroup = filterGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FilterSelection> getFiltersData() {
        return filtersData;
    }

    public void setFiltersData(List<FilterSelection> filtersData) {
        this.filtersData = filtersData;
    }

    public FilterGroup getFilterGroup() {
        return filterGroup;
    }

    public void setFilterGroup(FilterGroup filterGroup) {
        this.filterGroup = filterGroup;
    }
}
