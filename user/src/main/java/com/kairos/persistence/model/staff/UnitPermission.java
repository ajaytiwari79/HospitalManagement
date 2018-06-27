package com.kairos.persistence.model.staff;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.enums.EmploymentStatus;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.access_permission.AccessPage;
import com.kairos.persistence.model.user.unit_position.UnitPosition;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by prabjot on 24/10/16.
 * @Modified by vipul
 * removed fields for KP-2546
 */
@NodeEntity
public class UnitPermission extends UserBaseEntity {

    private String place;
    private long startDate;
    private long endDate;
    private int weeklyHours;

    @Relationship(type = APPLICABLE_IN_UNIT)
    private Organization organization;

    @Relationship(type = HAS_ACCESS_GROUP)
    private AccessGroup accessGroup;


    public UnitPermission() {
    }

    public String getPlace() {
        return place;
    }

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }


    public void setPlace(String place) {
        this.place = place;
    }


    public void setEndDate(long endDate) {
        this.endDate = endDate;
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

    public int getWeeklyHours() {
        return weeklyHours;
    }

    public void setWeeklyHours(int weeklyHours) {
        this.weeklyHours = weeklyHours;
    }

    public AccessGroup getAccessGroup() {
        return accessGroup;
    }

    public void setAccessGroup(AccessGroup accessGroup) {
        this.accessGroup = accessGroup;
    }


}