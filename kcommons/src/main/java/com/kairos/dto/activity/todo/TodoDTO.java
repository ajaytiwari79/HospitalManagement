package com.kairos.dto.activity.todo;

import com.kairos.enums.shift.TodoStatus;
import com.kairos.enums.todo.TodoSubtype;
import com.kairos.enums.todo.TodoType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created by pradeep
 * Created at 25/6/19
 **/
@Getter
@Setter
public class TodoDTO {

    private BigInteger id;
    private TodoType type;
    private TodoSubtype subtype;
    private BigInteger entityId;
    private BigInteger subEntityId;
    private TodoStatus status;
    private LocalDate shiftDate;
    private Date shiftDateTime;
    private Date requestedOn;
    private String description;
    private Long staffId;
    private Long employmentId;
    private Long unitId;
    private LocalDate createdAt;
    private String activityName;
    private LocalDateTime approvedOn;
    private String remark;
    private String comment;
    private LocalDateTime disApproveOn;
    private LocalDateTime pendingOn;

}
