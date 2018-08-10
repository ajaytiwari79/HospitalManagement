package com.kairos.scheduler.persistence.repository;


import com.kairos.scheduler.persistence.model.unit_settings.UnitTimeZoneMapping;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.*;

import java.math.BigInteger;
import java.time.ZoneId;

@Repository
public interface UnitTimeZoneMappingRepository extends MongoRepository<UnitTimeZoneMapping,BigInteger> {

    ZoneId findZoneIdByUnitId(Long unitId);

    @Query("{'unitId' : ?0}")
    UnitTimeZoneMapping findByUnitId(Long unitId);

   List<UnitTimeZoneMapping> findAllByDeletedFalseAndUnitIdIn(List<Long> unitIds);
}
