package com.kairos.persistence.model.task_type;

/**
 * Created by oodles on 9/2/17.
 */
public enum AddressCode {
    UNIT("-1"),HOME("0");

    private String value;

    AddressCode(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }

    public static AddressCode getKey(String value) {
        for(AddressCode v : values())
            if(v.getValue().equalsIgnoreCase(value)) return v;
        throw new IllegalArgumentException();
    }

}
