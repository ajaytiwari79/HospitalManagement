package com.kairos.persistence.model.user.expertise;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.pay_group_area.PayGroupArea;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_PAY_GROUP_AREA;

public class FunctionalPaymentMatrix extends UserBaseEntity {
    @Relationship(type = HAS_PAY_GROUP_AREA)
    private Set<PayGroupArea> payGroupAreas;

    public FunctionalPaymentMatrix() {
        // dc
    }

    public Set<PayGroupArea> getPayGroupAreas() {
        return payGroupAreas;
    }

    public void setPayGroupAreas(Set<PayGroupArea> payGroupAreas) {
        this.payGroupAreas = payGroupAreas;
    }
}
