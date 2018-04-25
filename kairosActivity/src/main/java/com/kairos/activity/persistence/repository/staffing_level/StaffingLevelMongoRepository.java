package com.kairos.activity.persistence.repository.staffing_level;

import com.kairos.activity.persistence.model.staffing_level.StaffingLevel;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;


@Repository

public interface StaffingLevelMongoRepository extends MongoBaseRepository<StaffingLevel,BigInteger>,StaffingLevelCustomRepository{

    StaffingLevel findByUnitIdAndPhaseIdAndDeletedFalse(Long organizationId, Long phaseId);
    StaffingLevel findByUnitIdAndCurrentDateAndDeletedFalse(Long unitId, Date currentDate);
    boolean existsByUnitIdAndCurrentDateAndDeletedFalse(Long unitId, Date currentDate);
    List<StaffingLevel> findByUnitIdAndCurrentDateBetweenAndDeletedFalse(Long unitId, Date startDate, Date endDate);
    @Query("{'deleted':false,'unitId':?0,'currentDate':{'$gte':?1,'$lte':?2},'absenceStaffingLevelInterval':{'$ne': null}}")
    List<StaffingLevel> findByUnitIdAndCurrentDateBetweenAndDeletedFalseForAbsence(Long unitId, Date startDate, Date endDate);

}
