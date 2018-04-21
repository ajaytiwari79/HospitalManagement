package com.planner.responseDto.staffDto;

import com.planner.responseDto.commonDto.BaseDTO;
import com.planner.responseDto.skillDto.OptaSkillDTO;
import com.planner.responseDto.taskDto.OptaTaskDTO;

import java.util.List;

public class OptaStaffDTO extends BaseDTO{

    private String firstName;
    private String lastName;
    private List<OptaTaskDTO> optaTaskDTOList;
    private List<OptaSkillDTO> optaSkillDTOS;
    private Double costByPerHr;

    public List<OptaSkillDTO> getOptaSkillDTOS() {
        return optaSkillDTOS;
    }

    public void setOptaSkillDTOS(List<OptaSkillDTO> optaSkillDTOS) {
        this.optaSkillDTOS = optaSkillDTOS;
    }

    public Double getCostByPerHr() {
        return costByPerHr;
    }

    public void setCostByPerHr(Double costByPerHr) {
        this.costByPerHr = costByPerHr;
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

    public List<OptaTaskDTO> getOptaTaskDTOList() {
        return optaTaskDTOList;
    }

    public void setOptaTaskDTOList(List<OptaTaskDTO> optaTaskDTOList) {
        this.optaTaskDTOList = optaTaskDTOList;
    }

}
