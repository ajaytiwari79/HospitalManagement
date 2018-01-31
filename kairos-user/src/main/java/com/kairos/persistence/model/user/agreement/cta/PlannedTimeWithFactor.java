package com.kairos.persistence.model.user.agreement.cta;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Transient;
@NodeEntity
public class PlannedTimeWithFactor extends UserBaseEntity{
    private float scale;
    private boolean add;
    @Transient
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
