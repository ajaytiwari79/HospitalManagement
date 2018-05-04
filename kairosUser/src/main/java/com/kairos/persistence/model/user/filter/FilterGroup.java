package com.kairos.persistence.model.user.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.enums.FilterEntityType;
import com.kairos.persistence.model.user.access_permission.AccessPage;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.EnumString;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Set;

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

//    @Property(name = "filterTypes")
//    @EnumString(FilterEntityType.class)
    private Set<FilterEntityType> filterTypes;

    public FilterGroup(){
        // default constructor
    }

    public List<AccessPage> getAccessPages() {
        return accessPages;
    }

    public void setAccessPages(List<AccessPage> accessPages) {
        this.accessPages = accessPages;
    }

    public Set<FilterEntityType> getFilterTypes() {
        return filterTypes;
    }

    public void setFilterTypes(Set<FilterEntityType> filterTypes) {
        this.filterTypes = filterTypes;
    }
}
