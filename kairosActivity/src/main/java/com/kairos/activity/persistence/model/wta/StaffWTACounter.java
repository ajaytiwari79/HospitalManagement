package com.kairos.activity.persistence.model.wta;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZonedDateTime;

/**
 * @author pradeep
 * @date - 23/5/18
 */
@Document
public class StaffWTACounter extends MongoBaseEntity{

    private LocalDate startDate;
    private LocalDate endDate;
    private BigInteger ruleTemplateId;
    private Long unitPositionId;
    private Long unitId;
    private int count;


    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigInteger getRuleTemplateId() {
        return ruleTemplateId;
    }

    public void setRuleTemplateId(BigInteger ruleTemplateId) {
        this.ruleTemplateId = ruleTemplateId;
    }

    public Long getUnitPositionId() {
        return unitPositionId;
    }

    public void setUnitPositionId(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
