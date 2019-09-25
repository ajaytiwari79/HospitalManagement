package com.kairos.dto.activity.staffing_level;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Date;

/**
 * @author pradeep
 * @date - 28/12/18
 */

@Getter
@Setter
public class UpdatedStaffingLevelDTO {

    @NotNull(message = "message.staffingLevel.currentDate.not.exists")
    private LocalDate currentDate;
    @NotNull(message = "message.staffingLevel.updatedAt.not.exists")
    private Date updatedAt;
}
