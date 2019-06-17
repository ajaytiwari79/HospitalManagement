package com.kairos.dto.activity.shift;

import com.kairos.enums.shift.TodoStatus;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Created by pradeep
 * Created at 13/6/19
 **/
@Getter
@Setter
public class RequestAbsenceDTO {
    @NotNull
    private BigInteger shiftId;
    @NotNull
    private BigInteger activityId;
    @NotNull
    private Date startDate;
    @NotNull
    private Date endDate;
    private TodoStatus todoStatus = TodoStatus.PENDING;
}
