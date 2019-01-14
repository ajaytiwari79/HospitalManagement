package com.kairos.persistence.repository.payroll;

import com.kairos.persistence.model.payroll.OrganizationBankDetails;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 14/1/19
 */

public interface OrganizationBankDetailsRepository extends MongoBaseRepository<OrganizationBankDetails,BigInteger> {

    OrganizationBankDetails findByOrganizationIdAndDeletedFalse(Long organizationId);
}
