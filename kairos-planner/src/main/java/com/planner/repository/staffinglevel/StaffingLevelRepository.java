package com.planner.repository.staffinglevel;

import com.planner.domain.staffinglevel.StaffingLevel;
import com.planner.repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StaffingLevelRepository extends MongoBaseRepository<StaffingLevel,String> {
    List<StaffingLevel> getStaffingLevelsByUnitAndDates(Long unitId, LocalDate start,LocalDate end);
    List<StaffingLevel> getStaffingLevelsByUnitAndDates(Long unitId, List<LocalDate> dates);
}
