package com.kairos.dto.planner.vrp.vrpPlanning;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * @author pradeep
 * @date - 20/6/18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class ShiftDTO {

    private String id;
    private String kairosShiftId;
    private String name;
    private Long staffId;
    private EmployeeDTO employee;
    private LocalDate localDate;
    private Date startTime;
    private Date endTime;
    private List<TaskDTO> tasks;
    private String color;


    public ShiftDTO(String id,String name, EmployeeDTO employee, LocalDate localDate, Date startTime, Date endTime) {
        this.id = id;
        this.employee = employee;
        this.localDate = localDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.name = name;
    }
}
