package com.kairos.persistence.model.wta;

import com.kairos.persistence.model.common.MongoBaseEntity;
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
    private boolean userHasStaffRole;


    public StaffWTACounter(LocalDate startDate, LocalDate endDate, BigInteger ruleTemplateId, Long unitPositionId, Long unitId, int count) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.ruleTemplateId = ruleTemplateId;
        this.unitPositionId = unitPositionId;
        this.unitId = unitId;
        this.count = count;
    }

    public StaffWTACounter() {
    }


    public boolean isUserHasStaffRole() {
        return userHasStaffRole;
    }

    public void setUserHasStaffRole(boolean userHasStaffRole) {
        this.userHasStaffRole = userHasStaffRole;
    }

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
