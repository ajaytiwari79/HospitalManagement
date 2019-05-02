package com.planner.domain.citizen;

import com.planner.domain.common.BaseEntity;

import java.util.List;

//import org.springframework.data.cassandra.core.mapping.Table;

//@Table
public class PlanningCitizen extends BaseEntity {


    private String locationId;
    private String firstName;
    private String lastName;
    private List<String> preferedStaff;
    private List<String> forbidenStaff;



    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<String> getPreferedStaff() {
        return preferedStaff;
    }

    public void setPreferedStaff(List<String> preferedStaff) {
        this.preferedStaff = preferedStaff;
    }

    public List<String> getForbidenStaff() {
        return forbidenStaff;
    }

    public void setForbidenStaff(List<String> forbidenStaff) {
        this.forbidenStaff = forbidenStaff;
    }
}
