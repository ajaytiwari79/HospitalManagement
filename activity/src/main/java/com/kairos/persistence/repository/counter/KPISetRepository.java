package com.kairos.persistence.repository.counter;
/*
 *Created By Pavan on 29/4/19
 *
 */

import com.kairos.dto.activity.counter.kpi_set.KPISetDTO;
import com.kairos.persistence.model.counter.KPISet;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface KPISetRepository extends MongoBaseRepository<KPISet,BigInteger> {

    List<KPISetDTO> findAllByReferenceIdAndDeletedFalse(Long countryId);

    @Query("{'deleted':false,'_id':?0}")
    KPISetDTO findOneById(BigInteger kpiSetId);
}
