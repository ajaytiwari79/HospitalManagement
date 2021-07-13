package com.kairos.persistence.repository.counter;

import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.persistence.model.Shift;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class ShiftMongoRepository {
    public List<Shift> findShiftsByKpiFilters(List<Long> kpiDatum, List<Long> longs, List<String> objects, Set<BigInteger> objects1, Date startDate, Date endDate) {
        return null;
    }
    public List<Shift> findAllByIdInAndDeletedFalseOrderByStartDateAsc(List<BigInteger> shiftIds) {
        return null;
    }
    public List<ShiftWithActivityDTO> findShiftsByShiftAndActvityKpiFilters(List<Long> staffIds, List<Long> longs, List<BigInteger> objects, List<Integer> dayOfWeeksNo, Date startDate, Date endDate, Boolean b) {
        return null;
    }
    public List<Shift> findShiftBetweenDurationAndUnitIdAndDeletedFalse(Date startDate, Date endDate, List<Long> longs) {
        return null;
    }
    public List<Shift> findAllShiftsByStaffIdsAndDate(List<Long> staffIds, LocalDateTime localDateTimeFromLocalDate, LocalDateTime localDateTimeFromLocalDate1) {
        return null;
    }
}
