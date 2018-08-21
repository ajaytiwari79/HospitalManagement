package com.kairos.activity.staffing_level;
/*
 *Created By Pavan on 14/8/18
 *
 */

import com.kairos.util.DateUtils;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class StaffingLevelFromTemplateDTO {
    private BigInteger templateId;
    private Set<Long> selectedDayTypeIds;
    private List<DateWiseActivityDTO> dateWiseActivityDTO;

    public StaffingLevelFromTemplateDTO() {
        //Default Constructor
    }


    public BigInteger getTemplateId() {
        return templateId;
    }

    public void setTemplateId(BigInteger templateId) {
        this.templateId = templateId;
    }


    public Set<Long> getSelectedDayTypeIds() {
        return selectedDayTypeIds;
    }

    public void setSelectedDayTypeIds(Set<Long> selectedDayTypeIds) {
        this.selectedDayTypeIds = selectedDayTypeIds;
    }

    public List<DateWiseActivityDTO> getDateWiseActivityDTO() {
        return dateWiseActivityDTO;
    }

    public void setDateWiseActivityDTO(List<DateWiseActivityDTO> dateWiseActivityDTO) {
        this.dateWiseActivityDTO = dateWiseActivityDTO;
    }

}
