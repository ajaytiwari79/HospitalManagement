package com.kairos.activity.persistence.repository.attendence_setting;

import com.kairos.activity.persistence.model.attendence_setting.AttendanceSetting;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;


@Repository
public interface AttendanceSettingRepository extends MongoBaseRepository<AttendanceSetting,BigInteger> {

    @Query(value ="{userId:?0,currentDate:?1,deleted:false}" )
    AttendanceSetting findbyUserIdAndDate(Long userId,LocalDate date);



}
