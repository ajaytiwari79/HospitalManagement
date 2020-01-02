package com.kairos.persistence.model.pay_table;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.Level;
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
import static org.neo4j.ogm.annotation.Relationship.INCOMING;

/**
 * Created by prabjot on 21/12/17.
 */
@NodeEntity
@Getter
@Setter
@NoArgsConstructor
public class PayTable extends UserBaseEntity {
    private String name;
    private String shortName;
    @Relationship(type = IN_ORGANIZATION_LEVEL)
    private Level level;
    private LocalDate startDateMillis;
    private LocalDate endDateMillis;
    private String paymentUnit;
    @Relationship(type = HAS_PAY_GRADE)
    private List<PayGrade> payGrades=new ArrayList<>();
    private String description;
    private boolean published;
    private boolean hasTempCopy;
    private boolean editable = true;

    @Relationship(type = HAS_TEMP_PAY_TABLE, direction = INCOMING)
    private PayTable payTable;
    private BigDecimal percentageValue; // this value is being used to update paygrade and functional amount

    public List<PayGrade> getPayGrades() {
        return payGrades=Optional.ofNullable(payGrades).orElse(new ArrayList<>());
    }

    public PayTable(String name, String shortName, String description, Level level, LocalDate startDateMillis, LocalDate endDateMillis, String paymentUnit, boolean editable) {
        this.name = name;
        this.description = description;
        this.shortName = shortName;
        this.level = level;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.paymentUnit = paymentUnit;
        this.editable = editable;
    }

}
