package com.kairos.activity.persistence.repository.clock_setting;

import com.kairos.activity.persistence.model.clock_setting.AttendanceSetting;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.time.LocalDate;

public interface AttendanceSettingRepository extends MongoBaseRepository<AttendanceSetting,BigInteger> {

    @Query(value ="{unitId:?0,staffId:?1,currentDate:?2,deleted:false}" )
    AttendanceSetting findbyUnitIdAndStaffIdAndDate(Long unitId, Long staffId,LocalDate date);
}
