package com.kairos.persistence.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TabKPI extends MongoBaseEntity {
    private Long unitId;
    private String tabId;
    private List<BigInteger> kpiIds;
}
