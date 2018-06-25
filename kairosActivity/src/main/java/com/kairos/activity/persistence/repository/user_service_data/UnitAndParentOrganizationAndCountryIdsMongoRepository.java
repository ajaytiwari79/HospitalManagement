package com.kairos.activity.persistence.repository.user_service_data;

import com.kairos.activity.persistence.model.user_service_data.UnitAndParentOrganizationAndCountryIds;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.activity.persistence.repository.phase.CustomPhaseMongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;

public interface UnitAndParentOrganizationAndCountryIdsMongoRepository extends MongoBaseRepository<UnitAndParentOrganizationAndCountryIds, BigInteger>, CustomPhaseMongoRepository {

    @Query(value = "{ unitId:?0}")
    UnitAndParentOrganizationAndCountryIds findByUnitId(Long unitId);
}
