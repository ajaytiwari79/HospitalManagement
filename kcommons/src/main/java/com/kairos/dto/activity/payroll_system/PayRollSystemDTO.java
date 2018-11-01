package com.kairos.dto.activity.payroll_system;

import java.math.BigInteger;

public class PayRollSystemDTO {

    protected BigInteger id;

    protected int code;
    protected String description;


    //====================================================

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
