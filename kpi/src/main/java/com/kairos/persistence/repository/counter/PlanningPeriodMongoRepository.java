package com.kairos.persistence.repository.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PlanningPeriodMongoRepository {
    public DateTimeInterval getPlanningPeriodIntervalByUnitId(Long unitId) {
        return null;
    }
    public List<PresenceTypeDTO> getAllPresenceTypeByCountry(Long countryId) {
        return null;
    }
}
