package com.kairos.persistence.model.organization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * Created by vipul on 27/2/18.
 */
@QueryResult
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationHierarchyData {
    private Unit parent;
    private List<Unit> childUnits;

    public OrganizationHierarchyData() {
        //Default Constructor
    }

    public Unit getParent() {
        return parent;
    }

    public void setParent(Unit parent) {
        this.parent = parent;
    }

    public List<Unit> getChildUnits() {
        return childUnits;
    }

    public void setChildUnits(List<Unit> childUnits) {
        this.childUnits = childUnits;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("OrganizationHierarchyData{");
        sb.append("parent=").append(parent);
        sb.append(", childUnits=").append(childUnits);
        sb.append('}');
        return sb.toString();
    }
}
