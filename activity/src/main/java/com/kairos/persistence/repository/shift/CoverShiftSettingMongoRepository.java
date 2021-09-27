package com.kairos.persistence.repository.shift;

import com.kairos.persistence.model.shift.CoverShiftSetting;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface CoverShiftSettingMongoRepository extends MongoBaseRepository<CoverShiftSetting, BigInteger> {

    @Query("{unitId:?0,deleted:false}")
    CoverShiftSetting getCoverShiftSettingByUnitId(Long unitId);
}
