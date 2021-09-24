package com.kairos.persistence.repository.counter;

import com.kairos.dto.activity.counter.configuration.KPIDTO;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.fibonacci_kpi.FibonacciKPIDTO;
import com.kairos.persistence.model.counter.FibonacciKPI;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public interface FibonacciKPIRepository extends MongoBaseRepository<FibonacciKPI, BigInteger> ,CustomFibonacciKPIRepository{

    @Query("{'deleted':false,'_id':?0}")
    FibonacciKPI findFibonacciKPIById(BigInteger id);

    @Query("{deleted:false,referenceId:?0,confLevel:?1}")
    List<FibonacciKPIDTO> findAllFibonacciKPIByCountryId(Long referenceId, ConfLevel confLevel);

    boolean existsByIdIn(Set<BigInteger> kpiIds);
    List<KPIDTO> findAllFibonacciKPIByReferenceId(Long referenceId, ConfLevel confLevel);
}
