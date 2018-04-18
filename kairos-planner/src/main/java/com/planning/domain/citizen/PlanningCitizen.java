package com.planning.domain.citizen;

import com.planning.domain.common.BaseEntity;
import org.springframework.data.cassandra.mapping.Table;

import java.util.List;

@Table
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
