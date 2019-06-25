package com.kairos.persistence.model.todo;

import com.kairos.enums.shift.TodoStatus;
import com.kairos.enums.todo.TodoSubtype;
import com.kairos.enums.todo.TodoType;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;

/**
 * Created by pradeep
 * Created at 25/6/19
 **/
@Getter
@Setter
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
}
