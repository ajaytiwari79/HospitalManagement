package com.kairos.activity.persistence.repository.attendence_setting;

import com.kairos.activity.persistence.model.attendence_setting.AttendanceSetting;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;


@Repository
public interface AttendanceSettingRepository extends MongoBaseRepository<AttendanceSetting,BigInteger> {

    @Query(value ="{unitId:?0,staffId:?1,currentDate:?2,deleted:false}" )
    AttendanceSetting findbyUnitIdAndStaffIdAndDate(Long unitId, Long staffId,LocalDate date);



}
