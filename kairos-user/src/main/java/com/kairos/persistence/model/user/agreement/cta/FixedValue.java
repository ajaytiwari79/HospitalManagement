package com.kairos.persistence.model.user.agreement.cta;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.country.Currency;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

@NodeEntity
public class FixedValue extends UserBaseEntity {
    private float amount;
    @Relationship(type = BELONGS_TO)
    private Currency currency;
    private Type type;

    public FixedValue() {
    }

    public FixedValue(float amount, Currency currency, Type type) {
        this.amount = amount;
        this.currency = currency;
        this.type = type;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public  enum Type{
        PER_DAY,PER_ACTIVITY,PER_TASK;
    }

}
