package com.kairos.persistence.repository.user.staff;

import com.kairos.persistence.model.access_permission.query_result.DayTypeCountryHolidayCalenderQueryResult;
import com.kairos.persistence.model.auth.ReasonCode;
import com.kairos.persistence.model.query_wrapper.CountryHolidayCalendarQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by prabjot on 24/10/16.
 */
@Repository
public interface ReasonCodeGraphRepository extends Neo4jBaseRepository<ReasonCode, Long> {


    @Query("MATCH(p:ProtectedDaysOffSetting{deleted:false})-[:HAS_PROTECTED_DAYS_OFF_SETTINGS]-(exp:Expertise{deleted:false}) return DISTINCT id(p) as id," +
            "p.holidayId AS holidayId, p.publicHolidayDate AS publicHolidayDate, p.protectedDaysOff AS protectedDaysOff, p.dayTypeId AS dayTypeId,id(exp) AS expertiseId ")
   List<DayTypeCountryHolidayCalenderQueryResult> getDataOfProtectedDaysOffToTransferInActivity();

    @Query("MATCH(c:Country)-[:HAS_HOLIDAY]->(p:CountryHolidayCalender{deleted:false,isEnabled:true})-[:DAY_TYPE]-(dt:DayType) return DISTINCT id(p) as id,p.holidayTitle as holidayTitle,p.holidayDate as holidayDate,id(dt) AS dayTypeId,p.startTime as startTime,\n" +
            "p.endTime AS endTime,p.reOccuring AS reOccuring,p.description AS description,dt.holidayType AS holidayType,p.googleCalId AS googleCalId,id(c) as countryId")
    List<CountryHolidayCalendarQueryResult> getDataOfCHCToTransferInActivity();




}

