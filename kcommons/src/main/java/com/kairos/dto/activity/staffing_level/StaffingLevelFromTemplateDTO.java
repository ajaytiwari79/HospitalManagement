package com.kairos.dto.activity.staffing_level;
/*
 *Created By Pavan on 14/8/18
 *
 */

import java.math.BigInteger;
import java.util.List;

public class StaffingLevelFromTemplateDTO {

    private BigInteger templateId;
    private List<DateWiseActivityDTO> activitiesByDate;

    public StaffingLevelFromTemplateDTO() {
        //Default Constructor
    }

    public StaffingLevelFromTemplateDTO(BigInteger templateId, List<DateWiseActivityDTO> activitiesByDate) {
        this.templateId = templateId;
        this.activitiesByDate = activitiesByDate;
    }

    public BigInteger getTemplateId() {
        return templateId;
    }

    public void setTemplateId(BigInteger templateId) {
        this.templateId = templateId;
    }

    public List<DateWiseActivityDTO> getActivitiesByDate() {
        return activitiesByDate;
    }

    public void setActivitiesByDate(List<DateWiseActivityDTO> activitiesByDate) {
        this.activitiesByDate = activitiesByDate;
    }
}
