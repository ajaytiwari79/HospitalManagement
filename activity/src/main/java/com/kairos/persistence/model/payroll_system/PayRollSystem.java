package com.kairos.persistence.model.payroll_system;

import com.kairos.enums.payroll_system.PayRollType;
import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class PayRollSystem extends MongoBaseEntity {

    protected int code;
    protected PayRollType payRollType;
    protected String description;
    //===================================================
    public PayRollSystem(){}

    public PayRollSystem(int code, PayRollType payRollType, String description) {
        this.code = code;
        this.payRollType = payRollType;
        this.description = description;
    }
    //====================================================

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

    public PayRollType getPayRollType() {
        return payRollType;
    }

    public void setPayRollType(PayRollType payRollType) {
        this.payRollType = payRollType;
    }
}
