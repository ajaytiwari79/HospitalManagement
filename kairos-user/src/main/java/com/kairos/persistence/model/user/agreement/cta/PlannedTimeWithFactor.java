package com.kairos.persistence.model.user.agreement.cta;

import org.neo4j.ogm.annotation.Transient;

public class PlannedTimeWithFactor {
    private float scale;
    private boolean add;
    @Transient
    private boolean subtract;
    private AccountType accountType;

    public PlannedTimeWithFactor() {
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public boolean isAdd() {
        return add;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    public boolean isSubtract() {
        return add?false:true;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
}
