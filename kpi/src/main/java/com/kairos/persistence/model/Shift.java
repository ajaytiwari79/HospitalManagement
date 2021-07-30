package com.kairos.persistence.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
@Getter
@Setter
public class Shift {
    protected Date startDate;
    protected Date endDate;
    protected Integer shiftStartTime;//In Second
    protected Integer shiftEndTime;//In Second
    protected boolean disabled = false;
    @NotNull(message = "error.ShiftDTO.staffId.notnull")
    protected Long staffId;
    protected BigInteger phaseId;
    protected BigInteger planningPeriodId;
    @Indexed
    protected Long unitId;
    protected int scheduledMinutes;
    protected int durationMinutes;
    @NotEmpty(message = "message.shift.activity.empty")
    protected List<ShiftActivity> activities;
}
