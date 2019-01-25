package com.kairos.persistence.repository.payroll;
/*
 *Created By Pavan on 17/12/18
 *
 */

import com.kairos.dto.activity.payroll.BankDTO;
import com.kairos.persistence.model.payroll.Bank;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface BankRepository extends MongoBaseRepository<Bank,BigInteger>,CustomBankRepository {

    Bank getByIdAndDeletedFalse(BigInteger id);

    BankDTO findByIdAndDeletedFalse(BigInteger id);

    List<BankDTO> findAllByCountryIdAndDeletedFalseOrderByCreatedAtDesc(Long countryId);

}
