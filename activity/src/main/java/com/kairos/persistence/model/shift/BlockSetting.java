package com.kairos.persistence.model.shift;

import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

/**
 * Created By G.P.Ranjan on 3/12/19
 **/
@Getter
@Setter
@Document
@NoArgsConstructor
public class BlockSetting extends MongoBaseEntity {
    private static final long serialVersionUID = 6704524885848714134L;
    private Long unitId;
    private LocalDate date;
    private Map<Long, Set<BigInteger>> blockDetails;
    private Set<Long> blockedStaffForCoverShift;

    public BlockSetting(Long unitId, LocalDate date,Map<Long, Set<BigInteger>> blockDetails){
        this.unitId = unitId;
        this.date = date;
        this.blockDetails = blockDetails;
    }
}
