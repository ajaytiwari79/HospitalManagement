package com.kairos.response.dto.web.cta;

import java.util.ArrayList;
import java.util.List;

public class CTARuleTemplateDefaultDataWrapper {
    private List<AccessGroupDTO> accessGroupDTOS=new ArrayList<>();
    private List<DayTypeDTO> dayTypeDTOS=new ArrayList<>();
    private List<PhaseDTO> phaseDTOS=new ArrayList<>();
    private List<TimeTypeDTO> timeTypeDTOS=new ArrayList<>();
    private List<EmploymentTypeTypeDTO> employmentTypeTypeDTOS=new ArrayList<>();

    public CTARuleTemplateDefaultDataWrapper() {
        //default
    }

    public List<AccessGroupDTO> getAccessGroupDTOS() {
        return accessGroupDTOS;
    }

    public void setAccessGroupDTOS(List<AccessGroupDTO> accessGroupDTOS) {
        this.accessGroupDTOS = accessGroupDTOS;
    }

    public List<DayTypeDTO> getDayTypeDTOS() {
        return dayTypeDTOS;
    }

    public void setDayTypeDTOS(List<DayTypeDTO> dayTypeDTOS) {
        this.dayTypeDTOS = dayTypeDTOS;
    }

    public List<PhaseDTO> getPhaseDTOS() {
        return phaseDTOS;
    }

    public void setPhaseDTOS(List<PhaseDTO> phaseDTOS) {
        this.phaseDTOS = phaseDTOS;
    }

    public List<TimeTypeDTO> getTimeTypeDTOS() {
        return timeTypeDTOS;
    }

    public void setTimeTypeDTOS(List<TimeTypeDTO> timeTypeDTOS) {
        this.timeTypeDTOS = timeTypeDTOS;
    }

    public List<EmploymentTypeTypeDTO> getEmploymentTypeTypeDTOS() {
        return employmentTypeTypeDTOS;
    }

    public void setEmploymentTypeTypeDTOS(List<EmploymentTypeTypeDTO> employmentTypeTypeDTOS) {
        this.employmentTypeTypeDTOS = employmentTypeTypeDTOS;
    }


}
