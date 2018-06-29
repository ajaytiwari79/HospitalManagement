package com.kairos.persistence.repository.unit_settings;

import com.kairos.persistence.model.unit_settings.TimeAttendanceGracePeriod;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;

public interface TimeAttendanceGracePeriodRepository extends MongoBaseRepository<TimeAttendanceGracePeriod,BigInteger> {

    @Query(value ="{unitId:?0,deleted:false}")
    TimeAttendanceGracePeriod findByUnitId(Long unitId);

}
