package com.kairos.persistence.model.user.expertise;

import com.kairos.commons.utils.ObjectUtils;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.pay_table.PayGrade;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.io.Serializable;
import java.math.BigDecimal;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_BASE_PAY_GRADE;

/**
 * Created by vipul on 27/3/18.
 */
@NodeEntity
@Getter
@Setter
@NoArgsConstructor
public class SeniorityLevel extends UserBaseEntity implements Comparable<SeniorityLevel>,Serializable {
    private Integer from; // this is stored as years
    private Integer to;   // this is stored as years

    @Relationship(type = HAS_BASE_PAY_GRADE)
    private PayGrade payGrade;  // this is payGrade which is coming from payTable

    // TODO We are unclear about this just adding and make sure this will utilize in future.
    private BigDecimal pensionPercentage;
    private BigDecimal freeChoicePercentage;
    private BigDecimal freeChoiceToPension;
    private boolean published;


    public SeniorityLevel(Integer from, Integer to, BigDecimal pensionPercentage, BigDecimal freeChoicePercentage, BigDecimal freeChoiceToPension, boolean published) {
        this.from = from;
        this.to = to;
        this.pensionPercentage = pensionPercentage;
        this.freeChoicePercentage = freeChoicePercentage;
        this.freeChoiceToPension = freeChoiceToPension;
        this.published = published;
    }

    public SeniorityLevel(Long id,Integer from, Integer to, PayGrade payGrade, BigDecimal pensionPercentage, BigDecimal freeChoicePercentage, BigDecimal freeChoiceToPension, boolean published) {
        this.id=id;
        this.from = from;
        this.to = to;
        this.payGrade = payGrade;
        this.pensionPercentage = pensionPercentage;
        this.freeChoicePercentage = freeChoicePercentage;
        this.freeChoiceToPension = freeChoiceToPension;
        this.published = published;
    }

    @Override
    public int compareTo(SeniorityLevel seniorityLevel) {
        return this.from - seniorityLevel.from;
    }

    public boolean isSeniorityLevelChanged(SeniorityLevel seniorityLevel){
        if(!ObjectUtils.isEquals(this.from,seniorityLevel.from) || )
    }
}
