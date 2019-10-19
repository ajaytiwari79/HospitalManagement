package com.kairos.persistence.model.access_permission.query_result;

import com.kairos.persistence.model.access_permission.AccessGroup;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
@Getter
@Setter
public class AccessGroupDayTypesQueryResult {
    private AccessGroup accessGroup;
    private List<DayTypeCountryHolidayCalenderQueryResult> dayTypes;

}
