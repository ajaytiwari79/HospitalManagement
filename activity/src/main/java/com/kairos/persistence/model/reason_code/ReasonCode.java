package com.kairos.persistence.model.reason_code;

import com.kairos.enums.reason_code.ReasonCodeType;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

/**
 * Created by pavan on 23/3/18.
 */

@Document
@Getter
@Setter
@NoArgsConstructor
public class ReasonCode extends MongoBaseEntity {
    private String name;
    private String code;
    private String description;
    private ReasonCodeType reasonCodeType;
    private Long countryId;
    private Long unitId;
    // this is only persist when we create any Absence type reason code
    private BigInteger timeTypeId;

    public ReasonCode(String name, String code, String description, ReasonCodeType reasonCodeType, Long countryId, BigInteger timeTypeId) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.reasonCodeType = reasonCodeType;
        this.countryId = countryId;
        this.timeTypeId=timeTypeId;
    }

    public ReasonCode(String name, String code, String description, ReasonCodeType reasonCodeType,  BigInteger timeTypeId,Long countryId) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.reasonCodeType = reasonCodeType;
        this.countryId = countryId;
        this.timeTypeId=timeTypeId;
    }
}
