package com.kairos.dto.activity.cta;

import com.kairos.enums.cta.AccountType;

import java.util.Objects;

public class PlannedTimeWithFactor {
    private float scale;
    private boolean add;
    private boolean subtract;
    private AccountType accountType;

    public PlannedTimeWithFactor() {
    }

    public PlannedTimeWithFactor(float scale, boolean add, AccountType accountType) {
        this.scale = scale;
        this.add = add;
        this.accountType = accountType;
    }

    public static PlannedTimeWithFactor buildPlannedTimeWithFactor(float scale, boolean add, AccountType accountType){
        return new PlannedTimeWithFactor(scale,add,accountType);

    }


    public void setSubtract(boolean subtract) {
        this.subtract = subtract;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlannedTimeWithFactor that = (PlannedTimeWithFactor) o;
        return Float.compare(that.scale, scale) == 0 &&
                add == that.add &&
                subtract == that.subtract &&
                accountType == that.accountType;
    }

    @Override
    public int hashCode() {

        return Objects.hash(scale, add, subtract, accountType);
    }
}
