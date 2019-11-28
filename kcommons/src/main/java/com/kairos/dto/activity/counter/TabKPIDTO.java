package com.kairos.dto.activity.counter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TabKPIDTO {
    private Long unitId;
    private String tabId;
    private List<BigInteger> kpiIds;
}
