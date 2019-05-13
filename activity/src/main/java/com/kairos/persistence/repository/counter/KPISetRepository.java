package com.kairos.persistence.repository.counter;
/*
 *Created By Pavan on 29/4/19
 *
 */

import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.kpi_set.KPISetDTO;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.persistence.model.counter.KPISet;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface KPISetRepository extends MongoBaseRepository<KPISet,BigInteger>,CustomKPISetRepository {

    List<KPISetDTO> findAllByReferenceIdAndDeletedFalse(Long countryId);

    @Query("{'deleted':false,'_id':?0}")
    KPISetDTO findOneById(BigInteger kpiSetId);

    boolean existsByNameIgnoreCaseAndDeletedFalseAndReferenceIdAndIdNot(String name, Long referenceId, BigInteger id);

    boolean existsByPhaseIdAndTimeTypeAndDeletedFalseAndIdNot(BigInteger phaseId, TimeTypeEnum timeType,BigInteger id);

    List<KPISetDTO> findByPhaseIdAndReferenceIdAndConfLevel(BigInteger phaseId, Long referenceId, ConfLevel confLevel);
}
