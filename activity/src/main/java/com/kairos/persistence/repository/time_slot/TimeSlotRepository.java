package com.kairos.persistence.repository.time_slot;

import com.kairos.dto.user.country.time_slot.TimeSlotSetDTO;
import com.kairos.enums.TimeSlotType;
import com.kairos.enums.time_slot.TimeSlotMode;
import com.kairos.persistence.model.time_slot.TimeSlotSet;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface TimeSlotRepository extends MongoBaseRepository<TimeSlotSet, BigInteger> {

    List<TimeSlotSetDTO> getByUnitIdAndTimeSlotMode(@NotNull Long unitId, @NotNull TimeSlotMode timeSlotMode);

    List<TimeSlotSetDTO> getByUnitIdAndNameInAndAndTimeSlotModeAndTimeSlotType(@NotNull Long unitId, @NotNull Set<String> timeslotNames, @NotNull TimeSlotMode timeSlotMode, @NotNull TimeSlotType timeSlotType);

    TimeSlotSetDTO findById(Long timeSlotSetId);

    List<TimeSlotSetDTO> findBySystemGeneratedTimeSlotsIsTrue();

    List<TimeSlotSetDTO> findByUnitIdInAndTimeSlotTypeOrderByStartDate(List<Long> unitId, TimeSlotType timeSlotType);

    List<TimeSlotSetDTO> findByUnitIdAndTimeSlotModeAndTimeSlotTypeOrderByStartDate(Long unitId, TimeSlotMode timeSlotMode, TimeSlotType timeSlotType);

    TimeSlotSetDTO findOneByUnitIdAfterStartDateLimitOne(Long unitId, LocalDate endDate);

    List<TimeSlotSet> findTimeSlotByUnitIdAndBetweenStartDateAndEndDateAndTimeSlotType(Long unitId, LocalDate startDate, LocalDate endDate, TimeSlotType timeSlotType);
}
