package com.kairos.persistence.repository.payroll;
/*
 *Created By Pavan on 19/12/18
 *
 */

import com.kairos.persistence.model.payroll.PensionProvider;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.inject.Inject;

public class PensionProviderRepositoryImpl implements CustomPensionProviderRepository{
    @Inject
    private MongoTemplate mongoTemplate;
    @Override
    public PensionProvider findByNameOrPaymentNumber(String name, String paymentNumber) {
        return null;
    }
}
