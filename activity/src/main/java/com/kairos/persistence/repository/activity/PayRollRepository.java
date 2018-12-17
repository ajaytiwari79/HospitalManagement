package com.kairos.persistence.repository.activity;
/*
 *Created By Pavan on 14/12/18
 *
 */

import com.kairos.dto.activity.payroll.PayRollDTO;
import com.kairos.persistence.model.payroll.PayRoll;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
public interface PayRollRepository extends MongoBaseRepository<PayRoll,BigInteger> {

    PayRollDTO findByIdAndDeletedFalse(BigInteger id);

    List<PayRollDTO> findAllByDeletedFalse();

    List<PayRoll> findAllByDeletedFalseAndIdIn(Set<BigInteger> payRollIds);

    List<PayRollDTO> findByCountryIdsInAndDeletedFalse(Long countryId);

    boolean existsByNameIgnoreCaseAndDeletedFalse(String name);

    boolean existsByNameIgnoreCaseAndDeletedFalseAndIdNot(String name, BigInteger id);
}
