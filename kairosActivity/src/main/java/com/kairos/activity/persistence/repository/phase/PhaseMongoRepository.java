package com.kairos.activity.persistence.repository.phase;

import com.kairos.activity.client.dto.Phase.PhaseDTO;
import com.kairos.activity.persistence.model.phase.Phase;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by vipul on 25/9/17.
 */
public interface PhaseMongoRepository extends MongoBaseRepository<Phase, BigInteger>, CustomPhaseMongoRepository {
    @Query(value = "{ organizationId:?0 ,name:?1 ,disabled:false}")
    Phase findByNameAndDisabled(Long unitId, String name, boolean disabled);

    @Query(value = "{sequence:?0 ,countryId:?1 ,deleted:false}", count = true)
    long findBySequenceAndCountryIdAndDeletedFalse(Integer sequence, Long countryId);

    List<PhaseDTO> findByCountryIdAndDeletedFalse(Long countryId);

    List<Phase> findByOrganizationIdAndDeletedFalseAndDurationGreaterThan(Long unitId,Long duration);

    Boolean checkPhaseByName(BigInteger phaseId, String name);

}
