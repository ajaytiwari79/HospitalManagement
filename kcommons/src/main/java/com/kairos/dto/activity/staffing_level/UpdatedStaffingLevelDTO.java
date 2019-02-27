package com.kairos.dto.activity.staffing_level;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Date;

/**
 * @author pradeep
 * @date - 28/12/18
 */

public class UpdatedStaffingLevelDTO {

    @NotNull(message = "message.staffingLevel.currentDate.not.exists")
    private LocalDate currentDate;
    @NotNull(message = "message.staffingLevel.updatedAt.not.exists")
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
