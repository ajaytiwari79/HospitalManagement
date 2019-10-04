package com.kairos.persistence.model.organization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * Created by vipul on 27/2/18.
 */
@QueryResult
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class OrganizationHierarchyData {
    private Organization parent;
    private List<Unit> childUnits;
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("OrganizationHierarchyData{");
        sb.append("parent=").append(parent);
        sb.append(", childUnits=").append(childUnits);
        sb.append('}');
        return sb.toString();
    }
}
