package com.kairos.persistence.model.wta;

import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.LocalDate;

/**
 * @author pradeep
 * @date - 23/5/18
 */
@Document
public class StaffWTACounter extends MongoBaseEntity{

    private static final long serialVersionUID = 3250639003609271457L;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigInteger ruleTemplateId;
    private String ruleTemplateName;
    private Long employmentId;
    private Long unitId;
    private int count;
    private boolean userHasStaffRole;


    public StaffWTACounter(LocalDate startDate, LocalDate endDate, BigInteger ruleTemplateId, String ruleTemplateName, Long employmentId, Long unitId, boolean userHasStaffRole) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.ruleTemplateId = ruleTemplateId;
        this.ruleTemplateName = ruleTemplateName;
        this.employmentId = employmentId;
        this.unitId = unitId;
        this.userHasStaffRole = userHasStaffRole;

    }

    public StaffWTACounter() {
    }


    public String getRuleTemplateName() {
        return ruleTemplateName;
    }

    public void setRuleTemplateName(String ruleTemplateName) {
        this.ruleTemplateName = ruleTemplateName;
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

    public Long getEmploymentId() {
        return employmentId;
    }

    public void setEmploymentId(Long employmentId) {
        this.employmentId = employmentId;
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
