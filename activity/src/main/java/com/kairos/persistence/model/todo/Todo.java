package com.kairos.persistence.model.todo;

import com.kairos.enums.shift.TodoStatus;
import com.kairos.enums.todo.TodoSubtype;
import com.kairos.enums.todo.TodoType;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;

/**
 * Created by pradeep
 * Created at 25/6/19
 **/
@Getter
@Setter
@NoArgsConstructor
public class Todo extends MongoBaseEntity {

    private TodoType type;
    private TodoSubtype subtype;
    private BigInteger entityId;
    private BigInteger subEntityId;
    private TodoStatus status;
    private LocalDate shiftDate;
    private String description;
    private Long staffId;
    private Long employmentId;
    private Long unitId;
    private String activityName;
    private Date approvedOn;
    private String remark;
    private String comment;

    public Todo(TodoType type, TodoSubtype subtype, BigInteger entityId, BigInteger subEntityId, String activityName, TodoStatus status, LocalDate shiftDate, String description, Long staffId, Long employmentId, Long unitId,String remark) {
        this.type = type;
        this.subtype = subtype;
        this.entityId = entityId;
        this.subEntityId = subEntityId;
        this.status = status;
        this.shiftDate = shiftDate;
        this.description = description;
        this.staffId = staffId;
        this.employmentId = employmentId;
        this.unitId = unitId;
        this.activityName = activityName;
        this.remark = remark;
    }
}
