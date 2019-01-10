package com.kairos.dto.activity.staffing_level;

import java.time.LocalDate;
import java.util.Date;

/**
 * @author pradeep
 * @date - 28/12/18
 */

public class UpdatedStaffingLevelDTO {

    private LocalDate currentDate;
    private Date updatedAt;

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
