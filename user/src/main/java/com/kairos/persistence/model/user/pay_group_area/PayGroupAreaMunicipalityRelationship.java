package com.kairos.persistence.model.user.pay_group_area;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.region.Municipality;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_MUNICIPALITY;

/**
 * Created by vipul on 12/3/18.
 */
@RelationshipEntity(type = HAS_MUNICIPALITY)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayGroupAreaMunicipalityRelationship extends UserBaseEntity {

    @StartNode
    private PayGroupArea payGroupArea;
    @EndNode
    private Municipality municipality;

    private Long startDateMillis;

    private Long endDateMillis;

}
