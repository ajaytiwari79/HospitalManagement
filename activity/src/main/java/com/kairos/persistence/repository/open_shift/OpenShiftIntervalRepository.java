package com.kairos.persistence.repository.open_shift;

import com.kairos.persistence.model.open_shift.OpenShiftInterval;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.dto.activity.open_shift.OpenShiftIntervalDTO;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface OpenShiftIntervalRepository extends MongoBaseRepository<OpenShiftInterval,BigInteger> {

    List<OpenShiftInterval> findAllByCountryIdAndDeletedFalse(Long countryId);

    OpenShiftInterval findByIdAndCountryIdAndDeletedFalse(BigInteger openShiftIntervalId,Long countryId);

    @Query(value = "{'from' : { '$lt' : ?1}, 'to' : { '$gt' : ?0} , deleted:false ,'_id': {'$ne' : ?2}}",exists = true)
    boolean isIntervalInValid(Integer from,Integer to,BigInteger id);

    List<OpenShiftIntervalDTO> getAllByCountryIdAndDeletedFalse(Long countryId);
}
