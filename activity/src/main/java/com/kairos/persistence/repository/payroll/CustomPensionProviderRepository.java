package com.kairos.persistence.repository.payroll;
/*
 *Created By Pavan on 19/12/18
 *
 */

import com.kairos.persistence.model.payroll.PensionProvider;

import java.math.BigInteger;

public interface CustomPensionProviderRepository {

    PensionProvider findByNameOrPaymentNumber(String name,String paymentNumber);

    PensionProvider findByNameOrPaymentNumberAndIdNot(String name, String paymentNumber, BigInteger id);
}
