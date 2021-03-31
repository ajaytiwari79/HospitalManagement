package com.kairos.persistence.model.user.expertise;

import com.kairos.enums.shift.PaidOutFrequencyEnum;
import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

@NodeEntity
@Getter
@Setter
@NoArgsConstructor
public class FunctionalPayment extends UserBaseEntity {
    private static final long serialVersionUID = -5919769856113230632L;
    @Relationship(type = APPLICABLE_FOR_EXPERTISE)
    private ExpertiseLine expertiseLine;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean published;
    private PaidOutFrequencyEnum paymentUnit;
    @Relationship(type = FUNCTIONAL_PAYMENT_MATRIX)
    private List<FunctionalPaymentMatrix> functionalPaymentMatrices;


    @Relationship(type = VERSION_OF)
    private FunctionalPayment parentFunctionalPayment;

    private boolean hasDraftCopy = false;
    // this is kept for tracking to show how many percentage got increase via payTable
    private BigDecimal percentageValue;
    private boolean oneTimeUpdatedAfterPublish;

    public FunctionalPayment(ExpertiseLine expertiseLine, LocalDate startDate, LocalDate endDate, PaidOutFrequencyEnum paymentUnit) {
        this.expertiseLine = expertiseLine;
        this.startDate = startDate;
        this.endDate = endDate;
        this.published = false;
        this.hasDraftCopy = false;
        this.paymentUnit = paymentUnit;
    }

    public List<FunctionalPaymentMatrix> getFunctionalPaymentMatrices() {
        return Optional.ofNullable(functionalPaymentMatrices).orElse(new ArrayList<>());
    }


}
