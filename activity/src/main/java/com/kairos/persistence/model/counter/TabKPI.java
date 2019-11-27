package com.kairos.persistence.model.counter;

import com.kairos.persistence.model.common.MongoBaseEntity;
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
    private Long tabId;
    private List<BigInteger> kpiIds;
}
