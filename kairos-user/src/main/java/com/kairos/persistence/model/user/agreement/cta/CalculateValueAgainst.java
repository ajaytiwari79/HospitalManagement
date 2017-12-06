package com.kairos.persistence.model.user.agreement.cta;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

@NodeEntity
public class CalculateValueAgainst {
    private  String calculateValue;
    private float scale;
    @Relationship(type = BELONGS_TO)
    private FixedValue fixedValue;

    public CalculateValueAgainst() {
        //default constractor
    }
    public String getCalculateValue() {
        return calculateValue;
    }

    public void setCalculateValue(String calculateValue) {
        this.calculateValue = calculateValue;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public FixedValue getFixedValue() {
        return fixedValue;
    }

    public void setFixedValue(FixedValue fixedValue) {
        this.fixedValue = fixedValue;
    }




}
