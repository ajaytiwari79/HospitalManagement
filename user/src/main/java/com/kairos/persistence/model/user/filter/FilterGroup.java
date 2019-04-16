package com.kairos.persistence.model.user.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.access_permission.AccessPage;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.APPLICABLE_FOR;

/**
 * Created by prerna on 30/4/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
public class FilterGroup extends UserBaseEntity {

    @Relationship(type = APPLICABLE_FOR)
    private List<AccessPage> accessPages;


    private List<FilterType> filterTypes;

    public FilterGroup(){
        // default constructor
    }

    public List<AccessPage> getAccessPages() {
        return accessPages;
    }

    public void setAccessPages(List<AccessPage> accessPages) {
        this.accessPages = accessPages;
    }

    public List<FilterType> getFilterTypes() {
        return filterTypes;
    }

    public void setFilterTypes(List<FilterType> filterTypes) {
        this.filterTypes = filterTypes;
    }
}
