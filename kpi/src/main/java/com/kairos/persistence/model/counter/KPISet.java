package com.kairos.persistence.model.counter;
/*
 *Created By Pavan on 29/4/19
 *
 */

import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.enums.KPISetType;
import com.kairos.enums.TimeTypeEnum;

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
    private static final long serialVersionUID = 4051899614276154441L;
    private String name;
    private Set<BigInteger> kpiIds;
    private TimeTypeEnum timeType;
    private BigInteger phaseId;
    private Long referenceId;
    private ConfLevel confLevel;
    private KPISetType kpiSetType;
    private String shortName;

    public KPISet(BigInteger id,String name,BigInteger phaseId, Long referenceId,ConfLevel confLevel, TimeTypeEnum timeType, Set<BigInteger> kpiIds,String shortName) {
        this.id=id;
        this.name = name;
        this.kpiIds = kpiIds;
        this.timeType = timeType;
        this.phaseId = phaseId;
        this.referenceId = referenceId;
        this.confLevel = confLevel;
        this.shortName = shortName;
    }
}
