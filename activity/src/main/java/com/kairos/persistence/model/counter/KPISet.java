package com.kairos.persistence.model.counter;
/*
 *Created By Pavan on 29/4/19
 *
 */

import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.Set;

@Document
@Getter
@Setter
@NoArgsConstructor
public class KPISet extends MongoBaseEntity {
    private String name;
    private Set<BigInteger> kpiIds;
    private TimeTypeEnum timeType;
    private BigInteger phaseId;
    private Long referenceId;
    private ConfLevel confLevel;


}
