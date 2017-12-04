package com.kairos.response.dto.web.cta;

import com.kairos.persistence.model.user.access_permission.AccessGroup;
import com.kairos.persistence.model.user.country.Currency;

import java.util.ArrayList;
import java.util.List;

public class CTARuleTemplateDefaultDataWrapper {
    private List<AccessGroup> accessGroupDTOS=new ArrayList<>();
    private List<DayTypeDTO> dayTypeDTOS=new ArrayList<>();
    private List<PhaseDTO> phaseDTOS=new ArrayList<>();
    private List<TimeTypeDTO> timeTypeDTOS=new ArrayList<>();
    private List<ActivityTypeDTO> activityTypeDTOS=new ArrayList<>();
    private List<EmploymentTypeDTO> employmentTypeTypeDTOS=new ArrayList<>();
    private List<Currency> currencies=new ArrayList<>();


    public CTARuleTemplateDefaultDataWrapper() {
        //default
    }

    public List<AccessGroup> getAccessGroupDTOS() {
        return accessGroupDTOS;
    }

    public void setAccessGroupDTOS(List<AccessGroup> accessGroupDTOS) {
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

    public List<EmploymentTypeDTO> getEmploymentTypeTypeDTOS() {
        return employmentTypeTypeDTOS;
    }

    public void setEmploymentTypeTypeDTOS(List<EmploymentTypeDTO> employmentTypeTypeDTOS) {
        this.employmentTypeTypeDTOS = employmentTypeTypeDTOS;
    }


}
