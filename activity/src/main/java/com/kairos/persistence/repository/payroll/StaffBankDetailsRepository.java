package com.kairos.persistence.repository.payroll;

import com.kairos.persistence.model.payroll.StaffBankDetails;
import com.kairos.persistence.model.payroll.StaffPensionProviderDetails;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 14/1/19
 */
@Repository
public interface StaffBankDetailsRepository extends MongoBaseRepository<StaffBankDetails,BigInteger> {

    StaffBankDetails findByStaffIdAndDeletedFalse(Long staffId);
}
