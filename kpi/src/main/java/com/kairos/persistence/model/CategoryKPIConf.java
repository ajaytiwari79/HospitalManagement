package com.kairos.persistence.model;

import com.kairos.dto.activity.counter.enums.ConfLevel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryKPIConf extends MongoBaseEntity {
    private BigInteger kpiId;
    private BigInteger categoryId;
    private Long countryId;
    private Long unitId;
    private ConfLevel level;
}
