package com.planner.repository.staffinglevel;

import com.planner.domain.staffinglevel.StaffingLevel;
import com.planner.repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StaffingLevelRepository extends MongoBaseRepository<StaffingLevel,String> {
    @Query("{'deleted':false,'unitId':?0,'date':{'$gte':?1,'$lte':?2}}")
    List<StaffingLevel> getStaffingLevelsByUnitIdAndBetweenDate(Long unitId, LocalDate start,LocalDate end);
    @Query("{'deleted':false,'unitId':?0,'date':{'$gte':?1,'$lte':?2}}")
    List<StaffingLevel> getStaffingLevelsByUnitIdAndBetweenDate(Long unitId, List<LocalDate> dates);
}
