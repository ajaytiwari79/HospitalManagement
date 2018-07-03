package com.kairos.persistence.repository.user_service_data;

import com.kairos.persistence.model.user_service_data.UnitAndParentOrganizationAndCountryIds;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;

public interface UnitAndParentOrganizationAndCountryIdsMongoRepository extends MongoBaseRepository<UnitAndParentOrganizationAndCountryIds, BigInteger> {

    @Query(value = "{unitId:?0}")
    UnitAndParentOrganizationAndCountryIds findByUnitId(Long unitId);
}
