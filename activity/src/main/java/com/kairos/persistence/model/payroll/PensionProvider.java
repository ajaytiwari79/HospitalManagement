package com.kairos.persistence.model.payroll;/*
 *Created By Pavan on 19/12/18
 *
 */

import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class PensionProvider extends MongoBaseEntity {
    private String name;
    private String paymentNumber;

    public PensionProvider() {
        //Default Constructor
    }

    public PensionProvider(BigInteger id,String name, String paymentNumber) {
        this.id=id;
        this.name = name;
        this.paymentNumber = paymentNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPaymentNumber() {
        return paymentNumber;
    }

    public void setPaymentNumber(String paymentNumber) {
        this.paymentNumber = paymentNumber;
    }
}
