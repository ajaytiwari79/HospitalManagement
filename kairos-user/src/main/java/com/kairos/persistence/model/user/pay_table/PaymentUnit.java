package com.kairos.persistence.model.user.pay_table;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by prabjot on 21/12/17.
 */
public enum PaymentUnit {


    PER_HOUR("Per Hour"),PER_DAY("Per Day"),PER_WEEK("Per Week"),PER_MONTH("Per Month");

    private String name;

    private static Map<String,PaymentUnit> keyValue;

    PaymentUnit(String name) {
        this.name = name;
    };

    public static Map<String,PaymentUnit> getValues(){
        if(keyValue == null){
            keyValue = new HashMap<>();
            for(PaymentUnit paymentUnit : values()){
                keyValue.put(paymentUnit.name,paymentUnit);
            }
        }
        return keyValue;
    }
}
