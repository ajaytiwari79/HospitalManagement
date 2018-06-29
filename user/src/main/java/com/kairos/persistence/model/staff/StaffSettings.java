package com.kairos.persistence.model.staff;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_OPEN_SHIFT_SETTINGS;

@NodeEntity
public class StaffSettings extends UserBaseEntity {

    @Relationship(type = HAS_OPEN_SHIFT_SETTINGS)
    private StaffPreferences staffPreferences;

    public StaffSettings() {
        //Default Constructor
    }

    public StaffPreferences getStaffPreferences() {
        return staffPreferences=Optional.ofNullable(staffPreferences).orElse(new StaffPreferences());
    }

    public void setStaffPreferences(StaffPreferences staffPreferences) {
        this.staffPreferences = staffPreferences;
    }
}
