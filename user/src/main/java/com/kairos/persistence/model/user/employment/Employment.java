package com.kairos.persistence.model.user.employment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.EmploymentSubType;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.reason_code.ReasonCode;
import com.kairos.persistence.model.organization.Unit;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.user.expertise.Expertise;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by pawanmandhan on 24/7/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@NodeEntity
public class Employment extends UserBaseEntity {

    @Relationship(type = HAS_EXPERTISE_IN)
    private Expertise expertise;

    @Relationship(type = BELONGS_TO_STAFF, direction = "INCOMING")
    private Staff staff;

    @Relationship(type = SUPPORTED_BY_UNION)
    private Unit union;

    @Relationship(type = IN_UNIT)
    private Unit unit;

    @Relationship(type = HAS_REASON_CODE)
    private ReasonCode reasonCode;


    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate lastWorkingDate;
    private Long timeCareExternalId;
    private boolean published;
    @Relationship(type = HAS_EMPLOYMENT_LINES)
    private List<EmploymentLine> employmentLines;
    private EmploymentSubType employmentSubType;
    private float taxDeductionPercentage;
    //This is the Intial value of accumulatedTimebank
    private long accumulatedTimebankMinutes;
    private LocalDate accumulatedTimebankDate;

    public Employment() {

    }

    public Employment(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Employment(Unit unit, LocalDate startDate, Long timeCareExternalId, boolean published, float taxDeductionPercentage, long accumulatedTimebankMinutes, LocalDate accumulatedTimebankDate) {
        this.unit = unit;
        this.startDate = startDate;
        this.timeCareExternalId = timeCareExternalId;
        this.published=published;
        this.taxDeductionPercentage=taxDeductionPercentage;
        this.accumulatedTimebankMinutes = accumulatedTimebankMinutes;
        this.accumulatedTimebankDate = accumulatedTimebankDate;
    }

    public Expertise getExpertise() {
        return expertise;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }


    public Unit getUnion() {
        return union;
    }


    public void setUnion(Unit union) {
        this.union = union;
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

    public LocalDate getLastWorkingDate() {
        return lastWorkingDate;
    }

    public void setLastWorkingDate(LocalDate lastWorkingDate) {
        this.lastWorkingDate = lastWorkingDate;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Long getTimeCareExternalId() {
        return timeCareExternalId;
    }

    public void setTimeCareExternalId(Long timeCareExternalId) {
        this.timeCareExternalId = timeCareExternalId;
    }

    public ReasonCode getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(ReasonCode reasonCode) {
        this.reasonCode = reasonCode;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public List<EmploymentLine> getEmploymentLines() {
        return Optional.ofNullable(employmentLines).orElse(new ArrayList<>());
    }

    public void setEmploymentLines(List<EmploymentLine> employmentLines) {
        this.employmentLines = employmentLines;
    }

    public EmploymentSubType getEmploymentSubType() { return employmentSubType; }

    public void setEmploymentSubType(EmploymentSubType employmentSubType) { this.employmentSubType = employmentSubType; }

    public float getTaxDeductionPercentage() {
        return taxDeductionPercentage;
    }

    public void setTaxDeductionPercentage(float taxDeductionPercentage) {
        this.taxDeductionPercentage = taxDeductionPercentage;
    }

    public long getAccumulatedTimebankMinutes() {
        return accumulatedTimebankMinutes;
    }

    public void setAccumulatedTimebankMinutes(long accumulatedTimebankMinutes) {
        this.accumulatedTimebankMinutes = accumulatedTimebankMinutes;
    }

    public LocalDate getAccumulatedTimebankDate() {
        return accumulatedTimebankDate;
    }

    public void setAccumulatedTimebankDate(LocalDate accumulatedTimebankDate) {
        this.accumulatedTimebankDate = accumulatedTimebankDate;
    }



    @Override
    public String toString() {
        return "Employment{" +
                "expertise=" + expertise +
                ", staff=" + staff +
                ", union=" + union +
                ", unit=" + unit +
                ", reasonCode=" + reasonCode +
                ", timeCareExternalId=" + timeCareExternalId +
                ", published=" + published +
                '}';
    }
}