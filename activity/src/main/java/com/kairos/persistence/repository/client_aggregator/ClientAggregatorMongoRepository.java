package com.kairos.persistence.repository.client_aggregator;

import com.kairos.persistence.model.client_aggregator.ClientAggregator;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.wrapper.VisitatedHoursTasksCount;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by oodles on 12/7/17.
 */
public interface ClientAggregatorMongoRepository extends MongoBaseRepository<ClientAggregator, BigInteger>, CustomClientAggregatorRepository {

    ClientAggregator findByUnitIdAndCitizenId(Long unitId, Long citizenId);

    List<ClientAggregator> findAllByUnitId(Long unitId);

    List<ClientAggregator> findByCitizenIdIn(List<Long> citizenId, Sort sort);

    List<ClientAggregator> findByUnitIdAndCitizenIdIn(Long unitId, List<Long> citizenId);


    @Query(value="{'citizenId' : ?0, unitId:?1}", fields="{citizenId:1, unitId:1, visitatedHoursPerWeek:1,visitatedHoursPerMonth:1,visitatedMinutesPerMonth:1,visitatedTasksPerWeek:1,visitatedMinutesPerWeek:1,visitatedTasksPerMonth:1}")
    VisitatedHoursTasksCount findVisitationHoursAndTasksByCitizenIdIn(Long citizenId, Long unitId);


}
