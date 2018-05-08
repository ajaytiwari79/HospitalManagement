package com.kairos.persistance.model.enums;

public enum AccountTypeEnum {


    DEMO("demo"), TRAIL("trail"), TRAINING("training"),FULL("full");
    public String value;

    AccountTypeEnum(String value){
        this.value = value;
    }

    public static AccountTypeEnum getByValue(final String value){
        for (AccountTypeEnum accountType : AccountTypeEnum.values()) {
            if (accountType.value.equals(value)) {
                return accountType;
            }
        }
        return null;
    }

}
