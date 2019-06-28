package com.kairos.dto.activity.shift;

import com.kairos.enums.shift.TodoStatus;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
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
    private String activityName;
    private Date startDate;
    private Date endDate;
    private TodoStatus todoStatus = TodoStatus.PENDING;
    private Long reasonCodeId;
    private String remarks;
}
