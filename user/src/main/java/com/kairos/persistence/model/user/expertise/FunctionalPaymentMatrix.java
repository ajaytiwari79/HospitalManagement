package com.kairos.persistence.model.user.expertise;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.pay_group_area.PayGroupArea;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_PAY_GROUP_AREA;
import static com.kairos.persistence.model.constants.RelationshipConstants.SENIORITY_LEVEL_FUNCTIONS;
@NodeEntity
@QueryResult
public class FunctionalPaymentMatrix extends UserBaseEntity implements Serializable {

    @Relationship(type = HAS_PAY_GROUP_AREA)
    private Set<PayGroupArea> payGroupAreas;
    @Relationship(type = SENIORITY_LEVEL_FUNCTIONS)
    private List<SeniorityLevelFunction> seniorityLevelFunction;

    public FunctionalPaymentMatrix() {
        // dc
    }

    public Set<PayGroupArea> getPayGroupAreas() {
        return payGroupAreas;
    }

    public void setPayGroupAreas(Set<PayGroupArea> payGroupAreas) {
        this.payGroupAreas = payGroupAreas;
    }

    public List<SeniorityLevelFunction> getSeniorityLevelFunction() {
        return seniorityLevelFunction;
    }

    public void setSeniorityLevelFunction(List<SeniorityLevelFunction> seniorityLevelFunction) {
        this.seniorityLevelFunction = seniorityLevelFunction;
    }
}
