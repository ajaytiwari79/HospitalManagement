package com.kairos.persistance.model.agreement_template;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigInteger;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClauseIdOrderIndex {

    private List<BigInteger> clauseIds;

    private List<Integer> orderedIndex;

    public List<BigInteger> getClauseIds() { return clauseIds; }

    public void setClauseIds(List<BigInteger> clauseIds) { this.clauseIds = clauseIds; }

    public List<Integer> getOrderedIndex() { return orderedIndex; }

    public void setOrderedIndex(List<Integer> orderedIndex) { this.orderedIndex = orderedIndex; }

    public ClauseIdOrderIndex(List<BigInteger> clauseIds, List<Integer> orderedIndex) {
        this.clauseIds = clauseIds;
        this.orderedIndex = orderedIndex;
    }
}
