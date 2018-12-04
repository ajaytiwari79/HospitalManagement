package com.kairos.persistence.repository.phase;


import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.dto.activity.phase.PhaseDTO;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

/**
 * Created by vipul on 25/9/17.
 */
@Repository
public interface PhaseMongoRepository extends MongoBaseRepository<Phase, BigInteger>, CustomPhaseMongoRepository {
    @Query(value = "{organizationId:?0 ,name:?1}")
    Phase findByUnitIdAndName(Long unitId, String name);

    @Query(value = "{organizationId:?0 ,'phaseEnum':?1}")
    Phase findByUnitIdAndPhaseEnum(Long unitId, String phaseEnum);


    @Query(value = "{sequence:?0 ,countryId:?1 ,deleted:false}", count = true)
    long findBySequenceAndCountryIdAndDeletedFalse(Integer sequence, Long countryId);

//    @Query(value = "{sequence:?0 ,organizationId:?1 ,deleted:false}")
//    Phase findBySequenceAndOrganizationIdAndDeletedFalse(Integer sequence, Long organizationId);

    List<PhaseDTO> findByCountryIdAndDeletedFalseOrderByPhaseTypeDescSequenceAsc(Long countryId);

    List<Phase> findByOrganizationIdAndPhaseTypeAndDeletedFalse(Long unitId, String PhaseType);

    List<Phase> findByOrganizationIdAndDeletedFalse(Long unitId);

    /**
     *@since 8-10-2018
     * @param unitId
     * @return
     */
    List<PhaseDTO> findByOrganizationIdAndDeletedFalseOrderByPhaseTypeDescSequenceAsc(Long unitId);

    @Query(value = "{'deleted':false,'organizationId':{'$in':?0}}",fields = "{'phaseEnum':1,'_id':1,'organizationId':1}")
    List<PhaseDTO> findAllPhaseOfUnitsByParentPhase(List<Long> unitIds);

    @Query(value = "{'deleted':false,'organizationId':{'$in':?0}}",fields = "{'phaseEnum':1,'_id':1,'organizationId':1}")
    List<Phase> findAllByUnitIdsAndDeletedFalse(List<Long> unitIds);

    @Query(value = "{'deleted':false,'countryId':?0}",fields = "{'phaseEnum':1,'_id':1,'organizationId':1}")
    List<Phase> findAllBycountryIdAndDeletedFalse(Long countryId);

}
