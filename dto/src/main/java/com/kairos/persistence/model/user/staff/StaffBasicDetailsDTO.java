package com.kairos.persistence.model.user.staff;

import com.kairos.persistence.model.enums.StaffStatusEnum;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Created by oodles on 3/2/17.
 */
public class StaffBasicDetailsDTO {

    private Long id;
    private String firstName;
    private String lastName;

    private List<Long> skills;
    private StaffStatusEnum currentStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public StaffStatusEnum getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(StaffStatusEnum currentStatus) {
        this.currentStatus = currentStatus;
    }

    public StaffBasicDetailsDTO(Long id, String firstName, String lastName, List<Long> skills, StaffStatusEnum currentStatus) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.skills = skills;
        this.currentStatus = currentStatus;
    }

    public StaffBasicDetailsDTO() {
    }
}
