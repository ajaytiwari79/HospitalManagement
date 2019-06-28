package com.kairos.persistence.model.shift;

import com.kairos.enums.shift.TodoStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.Date;

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
    private String activityName;
    private Date startDate;
    private Date endDate;
    private TodoStatus todoStatus;
    private Long reasonCodeId;
    private String remarks;
}
