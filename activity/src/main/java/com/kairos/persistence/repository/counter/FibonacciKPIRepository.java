package com.kairos.persistence.repository.counter;

import com.kairos.dto.activity.counter.configuration.KPIDTO;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.fibonacci_kpi.FibonacciKPIDTO;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.counter.FibonacciKPI;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.List;

public interface FibonacciKPIRepository extends MongoBaseRepository<FibonacciKPI, BigInteger> ,CustomFibonacciKPIRepository{

    @Query("{'deleted':false,'_id':?0}")
    FibonacciKPI findFibonacciKPIById(BigInteger id);

    @Query("{deleted:false,referenceId:?0,confLevel:?1}")
    List<KPIDTO> findAllFibonacciKPIByReferenceId(Long referenceId, ConfLevel confLevel);
}
