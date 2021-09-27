package com.kairos.persistence.repository.unit_settings;

import com.kairos.persistence.model.unit_settings.UnitGeneralSetting;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface UnitGeneralSettingRepository extends MongoBaseRepository<UnitGeneralSetting, BigInteger> {

    UnitGeneralSetting findByUnitId(Long unitId);
}
