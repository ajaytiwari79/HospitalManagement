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
    private List<DateWiseActivityDTO> importIntervals;

    public StaffingLevelFromTemplateDTO() {
        //Default Constructor
    }

    public StaffingLevelFromTemplateDTO(BigInteger templateId, List<DateWiseActivityDTO> importIntervals) {
        this.templateId = templateId;
        this.importIntervals = importIntervals;
    }

    public BigInteger getTemplateId() {
        return templateId;
    }

    public void setTemplateId(BigInteger templateId) {
        this.templateId = templateId;
    }

    public List<DateWiseActivityDTO> getImportIntervals() {
        return importIntervals;
    }

    public void setImportIntervals(List<DateWiseActivityDTO> importIntervals) {
        this.importIntervals = importIntervals;
    }
}
