package com.kairos.persistence.repository.staffing_level;

import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.persistence.model.staffing_level.StaffingLevelGraphConfiguration;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Repository
public interface StaffingLevelGraphConfigurationMongoRepository extends MongoBaseRepository<StaffingLevelGraphConfiguration,BigInteger>{

    @Query("{deleted:false,unitId:?0,userId:?1}")
    StaffingLevelGraphConfiguration findOneByUnitIdAndUserId(Long unitId, Long userId);

}
