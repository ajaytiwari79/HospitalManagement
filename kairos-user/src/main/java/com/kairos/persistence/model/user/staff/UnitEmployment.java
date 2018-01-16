package com.kairos.persistence.model.user.staff;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.enums.EmploymentStatus;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.position.UnitEmploymentPosition;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by prabjot on 24/10/16.
 */
@NodeEntity
public class UnitEmployment extends UserBaseEntity {

    private String place;
    private long startDate;
    private long endDate;
    private int weeklyHours;
    private int fullTime;
    private String dutyCalculationType;
    private String employmentType;
    private EmploymentStatus employmentStatus = EmploymentStatus.PENDING;
    private String employmentNumber;
    private boolean isUnitManagerEmployment;

    public void setUnitManagerEmployment(boolean unitManagerEmployment) {
        isUnitManagerEmployment = unitManagerEmployment;
    }

    public boolean isUnitManagerEmployment() {

        return isUnitManagerEmployment;
    }

    @Relationship(type = PROVIDED_BY)
    private Organization organization;


    @Relationship(type = HAS_UNIT_EMPLOYMENT_POSITION)
    private List<UnitEmploymentPosition> unitEmploymentPositions;

    @Relationship(type = HAS_WAGES)
    private List<Wage> wages = new ArrayList<>();

    @Relationship(type = HAS_PARTIAL_LEAVES)
    List<PartialLeave> partialLeaves = new ArrayList<>();


    public UnitEmployment(){}


    public String getPlace() {
        return place;
    }

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public String getDutyCalculationType() {
        return dutyCalculationType;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setPlace(String place) {
        this.place = place;
    }


    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public void setDutyCalculationType(String dutyCalculationType) {
        this.dutyCalculationType = dutyCalculationType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }


    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Organization getOrganization() {
        return organization;
    }

    public EmploymentStatus getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(EmploymentStatus employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public List<Wage> getWages() {
        return wages;
    }

    public void setWages(List<Wage> wages) {
        this.wages = wages;
    }

    public void setWeeklyHours(int weeklyHours) {
        this.weeklyHours = weeklyHours;
    }

    public void setFullTime(int fullTime) {
        this.fullTime = fullTime;
    }

    public int getWeeklyHours() {
        return weeklyHours;
    }

    public int getFullTime() {
        return fullTime;
    }

    public List<PartialLeave> getPartialLeaves() {
        return partialLeaves;
    }

    public void setPartialLeaves(List<PartialLeave> partialLeaves) {
        this.partialLeaves = partialLeaves;
    }

    public void setEmploymentNumber(String employmentNumber) {
        this.employmentNumber = employmentNumber;
    }

    public String getEmploymentNumber() {

        return employmentNumber;
    }

    public List<UnitEmploymentPosition> getUnitEmploymentPositions() {
        return Optional.ofNullable(unitEmploymentPositions).orElse(new ArrayList<UnitEmploymentPosition>());
    }

    public void setUnitEmploymentPositions(List<UnitEmploymentPosition> unitEmploymentPositions) {
        this.unitEmploymentPositions = unitEmploymentPositions;
    }
}
