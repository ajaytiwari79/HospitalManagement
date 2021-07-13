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
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

@Repository
public class CounterHelperRepository {
    public List<DayTypeDTO> findAllByCountryIdAndDeletedFalse(Long organizationId) {
        return null;
    }
    public List<CountryHolidayCalenderDTO> getAllByCountryIdAndHolidayDateBetween(Long countryId, LocalDate parse, LocalDate parse1) {
        return null;
    }
    public List<TimeSlotDTO> getUnitTimeSlot(Long organizationId) {
        return null;
    }
    public List<DayTypeDTO> getDayTypeWithCountryHolidayCalender(Long countryId) {
        return null;
    }
    public TimeSlotSetDTO findByUnitIdAndTimeSlotTypeOrderByStartDate(Long refId, TimeSlotType shiftPlanning) {
        return null;
    }
    public List<ActivityDTO> findAllActivityByDeletedFalseAndUnitId(List<Long> unitIds) {
        return null;
    }
    public List<PresenceTypeDTO> getAllPresenceTypeByCountry(Long countryId) {
        return null;
    }
    public ShortcutDTO getShortcutById(BigInteger shortcutId) {
        return null;
    }
    public Map<BigInteger, TimeTypeDTO> getAllTimeTypeWithItsLowerLevel(Long countryId, Collection<BigInteger> timeTypeIds) {
        return null;
    }
    public List<ActivityDTO> findAllByUnitIdAndTimeTypeIds(Long unitId, Set<BigInteger> lowerLevelTimeTypeIds) {
        return null;
    }
    public Collection<PlannedTimeType> getAllPlannedTimeByIds(List list) {
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
