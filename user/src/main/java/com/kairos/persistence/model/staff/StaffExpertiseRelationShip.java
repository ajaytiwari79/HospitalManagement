package com.kairos.persistence.model.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.annotations.*;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.user.expertise.Expertise;
import org.neo4j.ogm.annotation.*;
import org.neo4j.ogm.annotation.typeconversion.DateLong;

import java.util.Date;

import static com.kairos.persistence.model.constants.RelationshipConstants.STAFF_HAS_EXPERTISE;

/**
 * Created by pavan on 27/3/18.
 */
@KPermissionRelatedModel
@RelationshipEntity(type = STAFF_HAS_EXPERTISE)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StaffExpertiseRelationShip extends UserBaseEntity{
    @KPermissionRelationshipFrom
    @StartNode
    private Staff staff;
    @KPermissionRelationshipTo
    @EndNode
    private Expertise expertise;
    private Integer relevantExperienceInMonths;
    @DateLong
    private Date expertiseStartDate;
    private boolean unionRepresentative;

    public StaffExpertiseRelationShip() {
        //Default Constructor
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public Expertise getExpertise() {
        return expertise;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }

    public Integer getRelevantExperienceInMonths() {
        return relevantExperienceInMonths;
    }

    public void setRelevantExperienceInMonths(Integer relevantExperienceInMonths) {
        this.relevantExperienceInMonths = relevantExperienceInMonths;
    }

    public Date getExpertiseStartDate() {
        return expertiseStartDate;
    }

    public void setExpertiseStartDate(Date expertiseStartDate) {
        this.expertiseStartDate = expertiseStartDate;
    }

    public boolean isUnionRepresentative() {
        return unionRepresentative;
    }

    public void setUnionRepresentative(boolean unionRepresentative) {
        this.unionRepresentative = unionRepresentative;
    }

    public StaffExpertiseRelationShip(Long id, Staff staff, Expertise expertise, Integer relevantExperienceInMonths, Date expertiseStartDate) {
        this.id=id;
        this.staff = staff;
        this.expertise = expertise;
        this.relevantExperienceInMonths = relevantExperienceInMonths;
        this.expertiseStartDate=expertiseStartDate;
    }

    public StaffExpertiseRelationShip(Staff staff, Expertise expertise, Integer relevantExperienceInMonths, Date expertiseStartDate) {
        this.staff = staff;
        this.expertise = expertise;
        this.relevantExperienceInMonths = relevantExperienceInMonths;
        this.expertiseStartDate = expertiseStartDate;
    }

    @Override
    public String toString() {
        return "StaffExpertiseRelationShip{" +
                "staff=" + staff +
                ", expertise=" + expertise +
                ", relevantExperienceInMonths=" + relevantExperienceInMonths +
                '}';
    }
}
