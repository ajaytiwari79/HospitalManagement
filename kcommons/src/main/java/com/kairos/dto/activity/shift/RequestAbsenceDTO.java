package com.kairos.dto.activity.shift;

import com.kairos.enums.shift.TodoStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.ZonedDateTime;

/**
 * Created by pradeep
 * Created at 13/6/19
 **/
@Getter
@Setter
public class RequestAbsenceDTO {
    private BigInteger shiftId;
    private BigInteger activityId;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private TodoStatus todoStatus = TodoStatus.PENDING;
}
