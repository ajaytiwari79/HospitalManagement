package com.kairos.persistence.model.user.staff;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.Optional;

@QueryResult
public class StaffSettingsQueryResult {
    private Long id;
    private StaffSettings staffSettings;
    private StaffPreferences staffPreferences;

    public StaffSettingsQueryResult() {
        //Default Constructor
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StaffSettings getStaffSettings() {
        return staffSettings=Optional.ofNullable(staffSettings).orElse(new StaffSettings());
    }

    public void setStaffSettings(StaffSettings staffSettings) {
        this.staffSettings = staffSettings;
    }

    public StaffPreferences getStaffPreferences() {
        return staffPreferences=Optional.ofNullable(staffPreferences).orElse(new StaffPreferences());
    }

    public void setStaffPreferences(StaffPreferences staffPreferences) {
        this.staffPreferences = staffPreferences;
    }
}
