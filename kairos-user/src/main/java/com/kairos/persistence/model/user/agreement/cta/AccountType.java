package com.kairos.persistence.model.user.agreement.cta;

public enum  AccountType {
    DUTYTIME_ACCOUNT("DutyTime account"),TIMEBANK_ACCOUNT("TimeBank Account"),FLEX_ACCOUNT("Flex Account"),PAID_OUT("Paid Out");
    private String accountType;
    AccountType(String accountType){
        this.accountType=accountType;
    }

}
