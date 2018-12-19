package com.kairos.persistence.repository.payroll;
/*
 *Created By Pavan on 19/12/18
 *
 */

import com.kairos.dto.activity.payroll.PensionProviderDTO;
import com.kairos.persistence.model.payroll.PensionProvider;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface PensionProviderRepository extends MongoBaseRepository<PensionProvider,BigInteger>,CustomPensionProviderRepository {

    PensionProvider getByIdAndDeletedFalse(BigInteger id);

    PensionProviderDTO findByIdAndDeletedFalse(BigInteger id);

    List<PensionProviderDTO> findAllByCountryIdAndDeletedFalseOrderByCreatedAtDesc(Long countryId);
}
