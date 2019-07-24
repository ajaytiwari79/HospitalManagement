package com.kairos.persistence.repository.unit_settings;

import com.kairos.persistence.model.unit_settings.ProtectedDaysOffSetting;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * Created By G.P.Ranjan on 1/7/19
 **/
@Repository
public interface ProtectedDaysOffRepository extends MongoBaseRepository<ProtectedDaysOffSetting, BigInteger> {

    ProtectedDaysOffSetting getProtectedDaysOffByUnitIdAndDeletedFalse(Long unitId);

    @Query(value = "{deleted:false,unitId:{$in:?0}}")
    List<ProtectedDaysOffSetting> getAllProtectedDaysOffByUnitIdsAndDeletedFalse(List<Long> unitIds);

}
