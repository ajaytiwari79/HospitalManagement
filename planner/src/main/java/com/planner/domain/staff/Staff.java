package com.planner.domain.staff;

import com.planner.domain.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Objects;
@Document
public class Staff extends MongoBaseEntity {
    private String firstName;
    private String lastName;
    private List<Long> skills;

    public Staff(String firstName, String lastName, List<Long> skills) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.skills = skills;
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

    public List<Long> getSkills() {
        return skills;
    }

    public void setSkills(List<Long> skills) {
        this.skills = skills;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Staff staff = (Staff) o;
        return Objects.equals(firstName, staff.firstName) &&
                Objects.equals(lastName, staff.lastName) &&
                Objects.equals(skills, staff.skills);
    }

    @Override
    public int hashCode() {

        return Objects.hash(firstName, lastName, skills);
    }
}
