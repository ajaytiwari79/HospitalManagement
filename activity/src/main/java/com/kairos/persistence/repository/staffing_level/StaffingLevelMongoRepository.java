package com.kairos.persistence.repository.staffing_level;

import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Repository

public interface StaffingLevelMongoRepository extends MongoBaseRepository<StaffingLevel,BigInteger>,StaffingLevelCustomRepository{

    StaffingLevel findByUnitIdAndPhaseIdAndDeletedFalse(Long organizationId, Long phaseId);
    StaffingLevel findByUnitIdAndCurrentDateAndDeletedFalse(Long unitId, Date currentDate);
    boolean existsByUnitIdAndCurrentDateAndDeletedFalse(Long unitId, Date currentDate);
    List<StaffingLevel> findByUnitIdAndCurrentDateBetweenAndDeletedFalse(Long unitId, Date startDate, Date endDate);
    @Query("{deleted:false,unitId:?0,currentDate:{$gte:?1,$lte:?2}}")
    List<StaffingLevel> findByUnitIdAndCurrentDates(Long unitId, Date startDate, Date endDate);
    @Query("{deleted:false,unitId:?0,currentDate:{$in:?1}}")
    List<StaffingLevel> findByUnitIdAndCurrentDates(Long unitId, Set<LocalDate> localDates);

}
