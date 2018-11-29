package com.kairos.persistence.model.staff.permission;

import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.Organization;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.time.LocalDate;

import static com.kairos.persistence.model.constants.RelationshipConstants.APPLICABLE_IN_UNIT;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_ACCESS_GROUP;


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