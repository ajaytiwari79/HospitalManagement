package com.kairos.persistence.model.user.unit_position;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.country.reason_code.ReasonCode;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.staff.personal_details.Staff;
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
public class UnitPosition extends UserBaseEntity {

    @Relationship(type = HAS_EXPERTISE_IN)
    private Expertise expertise;

    @Relationship(type = BELONGS_TO_STAFF, direction = "INCOMING")
    private Staff staff;

    @Relationship(type = SUPPORTED_BY_UNION)
    private Organization union;

    @Relationship(type = IN_UNIT)
    private Organization unit;

    @Relationship(type = HAS_REASON_CODE)
    private ReasonCode reasonCode;


    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate lastWorkingDate;
    private Long timeCareExternalId;
    private boolean published;
    @Relationship(type = HAS_POSITION_LINES)
    private List<UnitPositionLine> unitPositionLines;
    private boolean mainUnitPosition;
    private float taxDeductionPercentage;

    public UnitPosition() {

    }

    public UnitPosition(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public UnitPosition(Organization unit, LocalDate startDate, Long timeCareExternalId,boolean published,float taxDeductionPercentage) {
        this.unit = unit;
        this.startDate = startDate;
        this.timeCareExternalId = timeCareExternalId;
        this.published=published;
        this.taxDeductionPercentage=taxDeductionPercentage;
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


    public Organization getUnion() {
        return union;
    }


    public void setUnion(Organization union) {
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

    public Organization getUnit() {
        return unit;
    }

    public void setUnit(Organization unit) {
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

    public List<UnitPositionLine> getUnitPositionLines() {
        return Optional.ofNullable(unitPositionLines).orElse(new ArrayList<>());
    }

    public void setUnitPositionLines(List<UnitPositionLine> unitPositionLines) {
        this.unitPositionLines = unitPositionLines;
    }



    public boolean isMainUnitPosition() {
        return mainUnitPosition;
    }

    public void setMainUnitPosition(boolean mainUnitPosition) {
        this.mainUnitPosition = mainUnitPosition;
    }

    public float getTaxDeductionPercentage() {
        return taxDeductionPercentage;
    }

    public void setTaxDeductionPercentage(float taxDeductionPercentage) {
        this.taxDeductionPercentage = taxDeductionPercentage;
    }

    @Override
    public String toString() {
        return "UnitPosition{" +
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