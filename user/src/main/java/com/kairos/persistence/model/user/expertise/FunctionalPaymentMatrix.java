package com.kairos.persistence.model.user.expertise;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.pay_group_area.PayGroupArea;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_PAY_GROUP_AREA;
import static com.kairos.persistence.model.constants.RelationshipConstants.SENIORITY_LEVEL_FUNCTIONS;
@NodeEntity
@Getter
@Setter
public class FunctionalPaymentMatrix extends UserBaseEntity implements Serializable {

    @Relationship(type = HAS_PAY_GROUP_AREA)
    private Set<PayGroupArea> payGroupAreas;
    @Relationship(type = SENIORITY_LEVEL_FUNCTIONS)
    private List<SeniorityLevelFunction> seniorityLevelFunction=new ArrayList<>();
}
