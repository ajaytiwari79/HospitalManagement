package com.kairos.persistence.model.client_exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.*;

/**
 * Created by oodles on 14/2/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Document
public class ClientException extends MongoBaseEntity {


    private Date fromTime;
    private Date toTime;
    private boolean isDeleted;


    public ClientException() {
        //Default Constructor
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    private String value;
    private String name;

    public BigInteger getExceptionTypeId() {
        return exceptionTypeId;
    }

    public void setExceptionTypeId(BigInteger exceptionTypeId) {
        this.exceptionTypeId = exceptionTypeId;
    }

    private BigInteger exceptionTypeId;

    private String description;
    private boolean exceptionHandled;

    private TaskOperation taskStatus;
    private String moveToDay;
    private Long moveToTimeSlotId;
    private boolean fullDay;

    private boolean isEnabled = true;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String info1;
    private String info2;
    private Long temporaryAddressId;
    private String newTaskDuration;
    private String newTaskPriority;
    private long unitId;

    private List<Long> houseHoldMembers;



    public long getUnitId() {
        return unitId;
    }

    public void setUnitId(long unitId) {
        this.unitId = unitId;
    }

    public Long getTemporaryAddressId() {
        return temporaryAddressId;
    }

    public void setTemporaryAddressId(Long temporaryAddressId) {
        this.temporaryAddressId = temporaryAddressId;
    }


    public String getInfo1() {
        return info1;
    }

    public void setInfo1(String info1) {
        this.info1 = info1;
    }

    public String getInfo2() {
        return info2;
    }

    public void setInfo2(String info2) {
        this.info2 = info2;
    }



    public String getNewTaskDuration() {
        return newTaskDuration;
    }

    public void setNewTaskDuration(String newTaskDuration) {
        this.newTaskDuration = newTaskDuration;
    }

    public String getNewTaskPriority() {
        return newTaskPriority;
    }

    public void setNewTaskPriority(String newTaskPriority) {
        this.newTaskPriority = newTaskPriority;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public Long getMoveToTimeSlotId() {
        return moveToTimeSlotId;
    }

    public void setMoveToTimeSlotId(Long moveToTimeSlotId) {
        this.moveToTimeSlotId = moveToTimeSlotId;
    }

    public String getMoveToDay() {
        return moveToDay;
    }

    public void setMoveToDay(String moveToDay) {
        this.moveToDay = moveToDay;
    }


    private long clientId;

    public long getClientId() {
        return clientId;
    }

    public void setFromTime(Date fromTime) {
        this.fromTime = fromTime;
    }

    public void setToTime(Date toTime) {
        this.toTime = toTime;
    }

    public Date getFromTime() {

        return fromTime;
    }

    public Date getToTime() {
        return toTime;
    }

    public boolean isExceptionHandled() {
        return exceptionHandled;
    }

    public void setExceptionHandled(boolean exceptionHandled) {
        this.exceptionHandled = exceptionHandled;
    }




    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFullDay() {
        return fullDay;
    }

    public void setFullDay(boolean fullDay) {
        this.fullDay = fullDay;
    }

    public List<Long> getHouseHoldMembers() {
        return Optional.ofNullable(houseHoldMembers).orElse(new ArrayList<>());
    }

    public void setHouseHoldMembers(List<Long> houseHoldMembers) {
        this.houseHoldMembers = houseHoldMembers;
    }


    public TaskOperation getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskOperation taskStatus) {
        this.taskStatus = taskStatus;
    }

    public enum TaskOperation{
        PREPONE,POSTPONE,CANCEL,NONE
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }



}
