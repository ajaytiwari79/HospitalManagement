package com.kairos.persistence.model.user.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.access_permission.AccessPage;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import static com.kairos.persistence.model.constants.RelationshipConstants.FILTER_BY_PAGE;

/**
 * Created by Jasgeet on 13/10/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NodeEntity
public class StaffFavouriteFilters extends UserBaseEntity {

    @Relationship(type = FILTER_BY_PAGE)
    private AccessPage accessPage;

    private String filterJson;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccessPage getAccessPage() {
        return accessPage;
    }

    public void setAccessPage(AccessPage accessPage) {
        this.accessPage = accessPage;
    }

    public String getFilterJson() {
        return filterJson;
    }

    public void setFilterJson(String filterJson) {
        this.filterJson = filterJson;
    }
}
