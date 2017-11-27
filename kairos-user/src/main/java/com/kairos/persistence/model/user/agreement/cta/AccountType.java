package com.kairos.persistence.model.user.agreement.cta;

public enum  AccountType {
    DUTYTIME_ACCOUNT("DutyTime account"),TIME_BANK("Time Bank"),FLEX_ACCOUNT("Flex Account");
    private String accountType;
    AccountType(String accountType){
        this.accountType=accountType;
    }

}
