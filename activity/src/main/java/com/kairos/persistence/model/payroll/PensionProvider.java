package com.kairos.persistence.model.payroll;


import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;


/**Created By Pavan on 19/12/18
 *
 */
@Document
public class PensionProvider extends MongoBaseEntity {
    private String name;
    private String paymentNumber;
    private Long countryId;

    public PensionProvider() {
        //Default Constructor
    }

    public PensionProvider(BigInteger id,String name, String paymentNumber,Long countryId) {
        this.id=id;
        this.name = name;
        this.paymentNumber = paymentNumber;
        this.countryId=countryId;
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

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

}
