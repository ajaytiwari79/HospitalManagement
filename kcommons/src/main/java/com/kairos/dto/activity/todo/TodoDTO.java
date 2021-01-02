package com.kairos.dto.activity.todo;

import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.enums.shift.TodoStatus;
import com.kairos.enums.todo.TodoSubtype;
import com.kairos.enums.todo.TodoType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

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
    private Map<String, TranslationInfo> translations;

    public TodoDTO(BigInteger id, TodoType type, TodoSubtype subtype, BigInteger entityId, BigInteger subEntityId, TodoStatus status, LocalDate shiftDate, Date shiftDateTime, Date requestedOn, String description, Long staffId, Long employmentId, Long unitId, LocalDate createdAt, String activityName, LocalDateTime approvedOn, String remark, String comment, LocalDateTime disApproveOn, LocalDateTime pendingOn) {
        this.id = id;
        this.type = type;
        this.subtype = subtype;
        this.entityId = entityId;
        this.subEntityId = subEntityId;
        this.status = status;
        this.shiftDate = shiftDate;
        this.shiftDateTime = shiftDateTime;
        this.requestedOn = requestedOn;
        this.description = description;
        this.staffId = staffId;
        this.employmentId = employmentId;
        this.unitId = unitId;
        this.createdAt = createdAt;
        this.activityName = activityName;
        this.approvedOn = approvedOn;
        this.remark = remark;
        this.comment = comment;
        this.disApproveOn = disApproveOn;
        this.pendingOn = pendingOn;
    }

    public String getActivityName(){
        return TranslationUtil.getName(translations,activityName);
    }

    public String getDescription(){
        return TranslationUtil.getDescription(translations,description);
    }
}
