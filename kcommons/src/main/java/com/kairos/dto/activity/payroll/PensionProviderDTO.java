package com.kairos.dto.activity.payroll;
/*
 *Created By Pavan on 19/12/18
 *
 */

import java.math.BigInteger;

public class PensionProviderDTO {
    private BigInteger id;
    private String name;
    private String paymentNumber;

    public PensionProviderDTO() {
        //Default Constructor
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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
