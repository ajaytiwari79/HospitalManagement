package com.kairos.wrappers.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.Gender;
import com.kairos.wrapper.TaskWrapper;

import java.util.List;

/**
 * Created by prabjot on 6/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffTaskDTO {

    private long id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private String cprNumber;
    private List<TaskWrapper> tasks;

    public List<TaskWrapper> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskWrapper> tasks) {
        this.tasks = tasks;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getCprNumber() {
        return cprNumber;
    }

    public void setCprNumber(String cprNumber) {
        this.cprNumber = cprNumber;
    }
}
