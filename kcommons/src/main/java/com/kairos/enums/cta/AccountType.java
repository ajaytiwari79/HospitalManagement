package com.kairos.enums.cta;

import java.io.Serializable;

public enum  AccountType implements Serializable {
    DUTYTIME_ACCOUNT("Duty Time Account"),TIMEBANK_ACCOUNT("Time Bank Account"),FLEX_ACCOUNT("Flex Account"),PAID_OUT("Paid Out");
    private String accountType;
    AccountType(String accountType){
        this.accountType=accountType;
    }
    public String toValue(){
        return accountType;
    }

}
