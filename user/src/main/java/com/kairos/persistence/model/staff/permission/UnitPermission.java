package com.kairos.persistence.model.staff.permission;

import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.Unit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by prabjot on 24/10/16.
 * @Modified by vipul
 * removed fields for KP-2546
 */
@NodeEntity
@Getter
@Setter
@NoArgsConstructor
public class UnitPermission extends UserBaseEntity {

    private String place;
    private long startDate;
    private long endDate;
    private int weeklyHours;

    @Relationship(type = APPLICABLE_IN_UNIT)
    private Unit unit;

    @Relationship(type = APPLICABLE_IN_ORGANIZATION)
    private Organization organization;

    @Relationship(type = HAS_ACCESS_GROUP)
    private AccessGroup accessGroup;
}