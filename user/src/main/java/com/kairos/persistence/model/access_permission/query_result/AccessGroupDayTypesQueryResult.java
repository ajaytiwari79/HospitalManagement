package com.kairos.persistence.model.access_permission.query_result;

import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.country.DayType;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class AccessGroupDayTypesQueryResult {
    AccessGroup accessGroup;
    List<DayType> dayTypes;

    public AccessGroup getAccessGroup() {
        return accessGroup;
    }

    public void setAccessGroup(AccessGroup accessGroup) {
        this.accessGroup = accessGroup;
    }

    public List<DayType> getDayTypes() {
        return dayTypes;
    }

    public void setDayTypes(List<DayType> dayTypes) {
        this.dayTypes = dayTypes;
    }
}
