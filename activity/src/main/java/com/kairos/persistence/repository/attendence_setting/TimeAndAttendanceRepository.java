package com.kairos.persistence.repository.attendence_setting;

import com.kairos.persistence.model.attendence_setting.TimeAndAttendance;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;


@Repository
public interface TimeAndAttendanceRepository extends MongoBaseRepository<TimeAndAttendance,BigInteger>,CustomTimeAndAttendanceRepository {

    @Query(value ="{unitId:?0,staffId:?1,currentDate:?2,deleted:false}" )
    TimeAndAttendance findbyUnitIdAndStaffIdAndDate(Long unitId, Long staffId, LocalDate date);

    @Query(value ="{attendanceDuration:{$elemMatch:{to:{$exists:false}}},unitId:?0,createdAt:{$lte:?1},deleted:false}" )
    List<TimeAndAttendance> findAllbyUnitIdAndDate(Long unitId, Date Startdate);

    @Query(value = "{deleted:false,shiftId:?0}")
    TimeAndAttendance findByShiftId(BigInteger shiftId);

}
