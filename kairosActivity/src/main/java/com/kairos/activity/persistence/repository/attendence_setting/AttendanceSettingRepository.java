package com.kairos.activity.persistence.repository.attendence_setting;

import com.kairos.activity.persistence.model.attendence_setting.AttendanceSetting;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;


@Repository
public interface AttendanceSettingRepository extends MongoBaseRepository<AttendanceSetting,BigInteger>,CustomAttendanceSettingRepository {

    @Query(value ="{'attendanceDuration.checkIn':?2,unitId:?0,staffId:?1,deleted:false}" )
    AttendanceSetting findByUnitIdAndStaffIdAndDate(Long unitId, Long staffId, Date date);
}
