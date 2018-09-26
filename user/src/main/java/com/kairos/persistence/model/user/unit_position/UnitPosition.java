package com.kairos.persistence.model.user.unit_position;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.country.functions.Function;
import com.kairos.persistence.model.country.reason_code.ReasonCode;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import com.kairos.persistence.model.user.position_code.PositionCode;
import com.kairos.persistence.model.staff.personal_details.Staff;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.math.BigInteger;
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

    @Relationship(type = HAS_POSITION_CODE)
    private PositionCode positionCode;

    @Relationship(type = BELONGS_TO_STAFF, direction = "INCOMING")
    private Staff staff;

    @Relationship(type = SUPPORTED_BY_UNION)
    private Organization union;

    @Relationship(type = IN_UNIT)
    private Organization unit;

    @Relationship(type = HAS_REASON_CODE)
    private ReasonCode reasonCode;

    @Relationship(type = HAS_SENIORITY_LEVEL)
    private SeniorityLevel seniorityLevel;

    @Relationship(type = HAS_FUNCTION)
    private List<Function> functions;

    private Long startDateMillis;
    private Long endDateMillis;
    private Long lastWorkingDateMillis;
    private Long timeCareExternalId;
    private boolean history;
    private boolean editable;
    private boolean published;
    @Relationship(type = HAS_POSITION_LINES)
    private List<PositionLine> positionLines;
    @Relationship(type = PARENT_UNIT_POSITION)
    private UnitPosition parentUnitPosition;

    public UnitPosition() {

    }

    public UnitPosition(PositionCode positionCode, Organization unit, Long startDateMillis, Long timeCareExternalId,boolean published) {
        this.positionCode = positionCode;
        this.unit = unit;
        this.startDateMillis = startDateMillis;
        this.timeCareExternalId = timeCareExternalId;
        this.editable=true;
        this.history=false;
        this.published=published;
    }

    public PositionCode getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(PositionCode positionCode) {
        this.positionCode = positionCode;
    }

    public Expertise getExpertise() {
        return expertise;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }


    public Long getStartDateMillis() {
        return startDateMillis;
    }

    public void setStartDateMillis(Long startDateMillis) {
        this.startDateMillis = startDateMillis;
    }

    public Long getEndDateMillis() {
        return endDateMillis;
    }

    public void setEndDateMillis(Long endDateMillis) {
        this.endDateMillis = endDateMillis;
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

    public Long getLastWorkingDateMillis() {
        return lastWorkingDateMillis;
    }

    public void setLastWorkingDateMillis(Long lastWorkingDateMillis) {
        this.lastWorkingDateMillis = lastWorkingDateMillis;
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

    public SeniorityLevel getSeniorityLevel() {
        return seniorityLevel;
    }

    public void setSeniorityLevel(SeniorityLevel seniorityLevel) {
        this.seniorityLevel = seniorityLevel;
    }

    public List<Function> getFunctions() {
        return functions;
    }

    public void setFunctions(List<Function> functions) {
        this.functions = functions;
    }

    public boolean isHistory() {
        return history;
    }

    public void setHistory(boolean history) {
        this.history = history;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public UnitPosition getParentUnitPosition() {
        return parentUnitPosition;
    }

    public void setParentUnitPosition(UnitPosition parentUnitPosition) {
        this.parentUnitPosition = parentUnitPosition;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public List<PositionLine> getPositionLines() {
        return Optional.ofNullable(positionLines).orElse(new ArrayList<>());
    }

    public void setPositionLines(List<PositionLine> positionLines) {
        this.positionLines = positionLines;
    }

    public UnitPosition(Long startDateMillis, Long endDateMillis, int totalWeeklyMinutes, float avgDailyWorkingHours, int workingDaysInWeek, float hourlyWages, Double salary) {
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
    }

    @Override
    public String toString() {
        return "UnitPosition{" +
                "expertise=" + expertise +
                ", positionCode=" + positionCode +
                ", staff=" + staff +
                ", union=" + union +
                ", unit=" + unit +
                ", reasonCode=" + reasonCode +
                ", seniorityLevel=" + seniorityLevel +
                ", functions=" + functions +
                ", startDateMillis=" + startDateMillis +
                ", endDateMillis=" + endDateMillis +
                ", lastWorkingDateMillis=" + lastWorkingDateMillis +
                ", timeCareExternalId=" + timeCareExternalId +
                ", history=" + history +
                ", editable=" + editable +
                ", published=" + published +
                ", parentUnitPosition=" + parentUnitPosition +
                '}';
    }
}