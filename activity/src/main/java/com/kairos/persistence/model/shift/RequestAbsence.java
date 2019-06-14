package com.kairos.persistence.model.shift;

import com.kairos.enums.shift.TodoStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.ZonedDateTime;

/**
 * Created by pradeep
 * Created at 13/6/19
 **/
@Getter
@Setter
@Document
public class RequestAbsence {
    private BigInteger shiftId;
    private BigInteger activityId;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private TodoStatus todoStatus;
}
