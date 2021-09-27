package com.kairos.persistence.model.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.reason_code.ReasonCodeType;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.organization.Unit;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.math.BigInteger;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

/**
 * Created by pavan on 23/3/18.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
@Getter
@Setter
public class ReasonCode extends UserBaseEntity {
    private static final long serialVersionUID = 5126696900810883123L;
    private String name;
    private String code;
    private String description;
    private ReasonCodeType reasonCodeType;
    @Relationship(type = BELONGS_TO)
    private Country country;
    @Relationship(type = BELONGS_TO)
    private Unit unit;
    // this is only persist when we create any Absence type reason code
    private BigInteger timeTypeId;
}
