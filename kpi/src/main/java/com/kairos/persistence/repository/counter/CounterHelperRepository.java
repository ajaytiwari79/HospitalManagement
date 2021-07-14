package com.kairos.persistence.repository.counter;

import com.kairos.dto.activity.ShortCuts.ShortcutDTO;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import com.kairos.dto.activity.staffing_level.presence.StaffingLevelDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.activity.todo.TodoDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotSetDTO;
import com.kairos.enums.TimeSlotType;
import com.kairos.persistence.model.PlannedTimeType;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

@Repository
public class CounterHelperRepository {

    @Inject private MongoTemplate mongoTemplate;

    public List<DayTypeDTO> findAllByCountryIdAndDeletedFalse(Long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("countryId").in(countryId).and("deleted").is(false)),
                Aggregation.lookup("countryHolidayCalender","_id","dayTypeId","countryHolidayCalenderData")
        );
        return mongoTemplate.aggregate(aggregation, "dayType",DayTypeDTO.class).getMappedResults();
    }
    public List<CountryHolidayCalenderDTO> getAllByCountryIdAndHolidayDateBetween(Long countryId, LocalDate startDate, LocalDate endDate) {
        return null;
    }
    public List<TimeSlotDTO> getUnitTimeSlot(Long organizationId) {
        return null;
    }

    public TimeSlotSetDTO findByUnitIdAndTimeSlotTypeOrderByStartDate(Long refId, TimeSlotType shiftPlanning) {
        return null;
    }
    public List<ActivityDTO> findAllActivityByDeletedFalseAndUnitId(List<Long> unitIds) {
        Query query = new Query(Criteria.where("unitId").in(unitIds).and("deleted").is(false));
        query.fields().include("name").include("id");
        return mongoTemplate.find(query,ActivityDTO.class,"activities");
    }
    public List<PresenceTypeDTO> getAllPresenceTypeByCountry(Long countryId) {
        return mongoTemplate.find(new Query(Criteria.where("countryId").is(countryId).and("deleted").is(false)),PresenceTypeDTO.class,"plannedTimeType");
    }
    public ShortcutDTO getShortcutById(BigInteger shortcutId) {
        return mongoTemplate.findOne(new Query(Criteria.where("id").is(shortcutId).and("deleted").is(false)),ShortcutDTO.class,"shortcut");
    }
    public Map<BigInteger, TimeTypeDTO> getAllTimeTypeWithItsLowerLevel(Long countryId, Collection<BigInteger> timeTypeIds) {
        return null;
    }
    public List<ActivityDTO> findAllByUnitIdAndTimeTypeIds(Long unitId, Set<BigInteger> lowerLevelTimeTypeIds) {
        return null;
    }
    public Collection<PlannedTimeType> getAllPlannedTimeByIds(List<BigInteger> plannedTimeTypeIds) {
        return null;
    }
    public List<ActivityDTO> findAllActivitiesByIds(Set set) {
        return null;
    }
    public List<TodoDTO> getAllTodoByEntityIds(Date startDate, Date endDate) {
        return null;
    }
    public List<TodoDTO> getAllTodoByShiftDate(Date startDate, Date endDate) {
        return null;
    }
    public List<TodoDTO> getAllTodoByDateTimeIntervalAndTodoStatus(Date startDate, Date endDate, List list) {
        return null;
    }
    public List<PhaseDTO> getPhasesByUnit(Long organizationId) {
        return null;
    }
    public List<StaffingLevelDTO> findByUnitIdAndDates(Long unitId, Date startDate, Date asDate) {
        return null;
    }
}
