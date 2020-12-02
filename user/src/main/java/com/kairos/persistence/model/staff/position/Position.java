package com.kairos.persistence.model.staff.position;

import com.kairos.enums.employment_type.EmploymentStatus;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.staff.permission.UnitPermission;
import com.kairos.persistence.model.staff.personal_details.Staff;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_UNIT_PERMISSIONS;

/**
 * Created by prabjot on 3/12/16.
 */
@NodeEntity
@Getter
@Setter
@NoArgsConstructor
public class Position extends UserBaseEntity {

    private String name;

    @Relationship(type = HAS_UNIT_PERMISSIONS)
    private List<UnitPermission> unitPermissions = new ArrayList<>();

    @Relationship(type = BELONGS_TO)
    private Staff staff;
    private Long endDateMillis;
    private Long startDateMillis;
    private Long accessGroupIdOnPositionEnd;
    private BigInteger reasonCodeId;


    public Position(String name, Staff staff) {
        this.name = name;
        this.staff = staff;
    }

    private EmploymentStatus employmentStatus = EmploymentStatus.PENDING;

    public List<UnitPermission> getUnitPermissions() {
        return Optional.ofNullable(unitPermissions).orElse(new ArrayList<>());
    }

}
