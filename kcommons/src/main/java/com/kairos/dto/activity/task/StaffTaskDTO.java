package com.kairos.dto.activity.task;

import com.kairos.enums.Gender;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StaffTaskDTO {

    private long id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private String cprNumber;
    private List<TaskWrapper> tasks;
}