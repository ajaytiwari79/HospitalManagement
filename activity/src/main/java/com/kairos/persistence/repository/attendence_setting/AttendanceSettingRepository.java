package com.kairos.persistence.repository.attendence_setting;

import com.kairos.persistence.model.attendence_setting.AttendanceSetting;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;


@Repository
public interface AttendanceSettingRepository extends MongoBaseRepository<AttendanceSetting,BigInteger>,CustomAttendanceSettingRepository {

    @Query(value ="{unitId:?0,staffId:?1,currentDate:?2,deleted:false}" )
    AttendanceSetting findbyUnitIdAndStaffIdAndDate(Long unitId, Long staffId,LocalDate date);

    @Query(value ="{attendanceDuration:{$elemMatch:{to:{$exists:false}}},unitId:?0,createdAt:{$lte:?1},deleted:false}" )
    List<AttendanceSetting> findAllbyUnitIdAndDate(Long unitId, Date Startdate);

    @Query(value = "{deleted:false,shiftId:?0,shiftState:?1}")
    AttendanceSetting findByShiftId(BigInteger shiftId,String shiftState);

    @Query(value = "{deleted:false,shiftId:{$in:?0},shiftState:?1}")
    List<AttendanceSetting> findAllByShiftIds(List<BigInteger> shiftIds,String shiftState);
}
