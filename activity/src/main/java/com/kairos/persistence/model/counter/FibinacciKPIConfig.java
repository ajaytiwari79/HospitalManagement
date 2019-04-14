package com.kairos.persistence.model.counter;



import com.kairos.enums.kpi.Direction;

import java.math.BigInteger;

public class FibinacciKPIConfig {

    private BigInteger id;
    private int impactWeight;
    private Direction sortingOrder;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public int getImpactWeight() {
        return impactWeight;
    }

    public void setImpactWeight(int impactWeight) {
        this.impactWeight = impactWeight;
    }

    public Direction getSortingOrder() {
        return sortingOrder;
    }

    public void setSortingOrder(Direction sortingOrder) {
        this.sortingOrder = sortingOrder;
    }
}
