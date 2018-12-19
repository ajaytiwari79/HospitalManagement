package com.kairos.persistence.repository.payroll;
/*
 *Created By Pavan on 19/12/18
 *
 */

import com.kairos.persistence.model.payroll.Bank;

import java.math.BigInteger;

public interface CustomBankRepository {

    Bank findByNameOrAccountNumber(String name, String internationalAccountNumber, String registrationNumber, String swiftCode);

    Bank findByNameOrAccountNumberAndIdNot(BigInteger id, String name, String internationalAccountNumber, String registrationNumber, String swiftCode);
}
