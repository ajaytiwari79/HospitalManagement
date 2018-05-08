package com.kairos.persistence.model.user.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.enums.FilterEntityType;
import com.kairos.persistence.model.user.access_permission.AccessPage;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by prerna on 30/4/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
public class FilterGroup extends UserBaseEntity {

    @Relationship(type = APPLICABLE_FOR)
    private List<AccessPage> accessPages;


    private List<FilterEntityType> filterTypes;

    public FilterGroup(){
        // default constructor
    }

    public List<AccessPage> getAccessPages() {
        return accessPages;
    }

    public void setAccessPages(List<AccessPage> accessPages) {
        this.accessPages = accessPages;
    }

    public List<FilterEntityType> getFilterTypes() {
        return filterTypes;
    }

    public void setFilterTypes(List<FilterEntityType> filterTypes) {
        this.filterTypes = filterTypes;
    }
}
