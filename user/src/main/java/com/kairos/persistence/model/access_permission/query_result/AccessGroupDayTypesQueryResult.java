package com.kairos.persistence.model.access_permission.query_result;

import com.kairos.persistence.model.access_permission.AccessGroup;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class AccessGroupDayTypesQueryResult {
    private AccessGroup accessGroup;
    //List<DayType> dayTypes;
    private List<DayTypeCountryHolidayCalenderQueryResult> dayTypes;

    public AccessGroup getAccessGroup() {
        return accessGroup;
    }

    public void setAccessGroup(AccessGroup accessGroup) {
        this.accessGroup = accessGroup;
    }

   /* public List<DayType> getDayTypes() {
        return dayTypes;
    }

    public void setDayTypes(List<DayType> dayTypes) {
        this.dayTypes = dayTypes;
    }*/

    /*public List<DayTypeCountryHolidayCalenderQueryResult> getDayTypesWithHolidayType() {
        return dayTypesWithHolidayType;
    }

    public void setDayTypesWithHolidayType(List<DayTypeCountryHolidayCalenderQueryResult> dayTypesWithHolidayType) {
        this.dayTypesWithHolidayType = dayTypesWithHolidayType;
    }*/

    public List<DayTypeCountryHolidayCalenderQueryResult> getDayTypes() {
        return dayTypes;
    }

    public void setDayTypes(List<DayTypeCountryHolidayCalenderQueryResult> dayTypes) {
        this.dayTypes = dayTypes;
    }
}
