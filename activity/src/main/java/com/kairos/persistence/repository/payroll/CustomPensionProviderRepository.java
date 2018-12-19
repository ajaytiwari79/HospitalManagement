package com.kairos.persistence.repository.payroll;
/*
 *Created By Pavan on 19/12/18
 *
 */

import com.kairos.persistence.model.payroll.PensionProvider;

public interface CustomPensionProviderRepository {

    PensionProvider findByNameOrPaymentNumber(String name,String paymentNumber);
}
