package com.kairos.activity.persistence.repository.unit_settings;

import com.kairos.activity.persistence.model.unit_settings.TimeAttendanceGracePeriod;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;

public interface TimeAttendanceGracePeriodRepository extends MongoBaseRepository<TimeAttendanceGracePeriod,BigInteger> {

    @Query(value ="{unitId:?0,deleted:false}")
    TimeAttendanceGracePeriod findByUnitId(Long unitId);

}
