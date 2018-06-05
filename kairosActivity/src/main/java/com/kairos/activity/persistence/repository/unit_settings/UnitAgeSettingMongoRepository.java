package com.kairos.activity.persistence.repository.unit_settings;

import com.kairos.activity.persistence.model.unit_settings.UnitAgeSetting;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.List;

public interface UnitAgeSettingMongoRepository extends MongoBaseRepository<UnitAgeSetting, BigInteger> {

    @Query(value = "{ unitId:?0 ,deleted:false}")
    UnitAgeSetting findByUnit(Long unitId);

    @Query(value = "{ 'unitId':{ '$in' : ?0 } ,deleted:false}")
    List<UnitAgeSetting> findByUnitIds(List<Long> unitIds);
}
