package com.kairos.persistence.repository.action;

import com.kairos.persistence.model.action.ActionInfo;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface ActionInfoRepository extends MongoBaseRepository<ActionInfo, BigInteger> {
    @Query("{'deleted':false, 'unitId':?0, 'staffId':?1}")
    Optional<ActionInfo> getByUnitIdAndStaffId(Long unitId, Long staffId);
}
