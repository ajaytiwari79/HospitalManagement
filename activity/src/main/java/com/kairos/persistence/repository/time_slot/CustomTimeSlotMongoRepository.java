package com.kairos.persistence.repository.time_slot;

import com.kairos.dto.user.country.time_slot.TimeSlotSetDTO;
import com.kairos.enums.TimeSlotType;

import java.util.List;

public interface CustomTimeSlotMongoRepository {

    List<TimeSlotSetDTO> findByUnitIdAndTimeSlotType(Long unitId, TimeSlotType timeSlotType);
}
