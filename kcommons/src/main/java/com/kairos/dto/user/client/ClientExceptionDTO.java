package com.kairos.dto.user.client;

import com.kairos.dto.user.organization.AddressDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by oodles on 14/2/17.
 */
public class ClientExceptionDTO {

    private boolean fullDay;
    private String exceptionTypeId;
    private Long clientId;
    private List<String> selectedDates;

    private String fromTime;
    private String toTime;

    private String moveToDay;
    private Long moveToTimeslotId;
    private String info1;
    private String info2;
    private Long temporaryAddress;

    private boolean updateTaskDuration;
    private boolean updateTaskPriority;
    private List<Long> houseHoldMembers;

    public boolean isUpdateTaskPriority() {
        return updateTaskPriority;
    }

    public void setUpdateTaskPriority(boolean updateTaskPriority) {
        this.updateTaskPriority = updateTaskPriority;
    }

    public void setUpdateTaskDuration(boolean updateTaskDuration) {
        this.updateTaskDuration = updateTaskDuration;
    }

    public boolean isUpdateTaskDuration() {

        return updateTaskDuration;
    }

    private int newTaskDuration;
    private int newTaskPriority;
    private int daysToReview = 1;


    public int getDaysToReview() {
        return daysToReview;
    }

    public void setDaysToReview(int daysToReview) {
        this.daysToReview = daysToReview;
    }

    private boolean exceptionHandled;

    private AddressDTO tempAddress;

    public AddressDTO getTempAddress() {
        return tempAddress;
    }

    public void setTempAddress(AddressDTO tempAddress) {
        this.tempAddress = tempAddress;
    }

    public boolean isExceptionHandled() {
        return exceptionHandled;
    }

    public void setExceptionHandled(boolean exceptionHandled) {
        this.exceptionHandled = exceptionHandled;
    }

    public Long getTemporaryAddress() {
        return temporaryAddress;
    }

    public void setTemporaryAddress(Long temporaryAddress) {
        this.temporaryAddress = temporaryAddress;
    }

    public void setNewTaskDuration(int newTaskDuration) {
        this.newTaskDuration = newTaskDuration;
    }

    public void setNewTaskPriority(int newTaskPriority) {
        this.newTaskPriority = newTaskPriority;
    }

    public int getNewTaskDuration() {

        return newTaskDuration;
    }

    public int getNewTaskPriority() {
        return newTaskPriority;
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

    public boolean isFullDay() {
        return fullDay;
    }

    public void setFullDay(boolean fullDay) {
        this.fullDay = fullDay;
    }

    public List<String> getSelectedDates() {
        return selectedDates;
    }

    public void setSelectedDates(List<String> selectedDates) {
        this.selectedDates = selectedDates;
    }


    public String getExceptionTypeId() {
        return exceptionTypeId;
    }

    public void setExceptionTypeId(String exceptionTypeId) {
        this.exceptionTypeId = exceptionTypeId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getMoveToDay() {
        return moveToDay;
    }

    public void setMoveToDay(String moveToDay) {
        this.moveToDay = moveToDay;
    }


    public Long getMoveToTimeslotId() {
        return moveToTimeslotId;
    }

    public void setMoveToTimeslotId(Long moveToTimeslotId) {
        this.moveToTimeslotId = moveToTimeslotId;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public List<Long> getHouseHoldMembers() {
        return Optional.ofNullable(houseHoldMembers).orElse(new ArrayList<>());
    }

    public void setHouseHoldMembers(List<Long> houseHoldMembers) {
        this.houseHoldMembers = houseHoldMembers;
    }

    @Override
    public String toString() {
        return "ClientExceptionDTO{" +
                "fullDay=" + fullDay +
                ", exceptionTypeId='" + exceptionTypeId + '\'' +
                ", clientId=" + clientId +
                ", selectedDates=" + selectedDates +
                ", fromTime='" + fromTime + '\'' +
                ", toTime='" + toTime + '\'' +
                ", moveToDay='" + moveToDay + '\'' +
                ", moveToTimeslotId=" + moveToTimeslotId +
                ", info1='" + info1 + '\'' +
                ", info2='" + info2 + '\'' +
                ", temporaryAddress=" + temporaryAddress +
                ", newTaskDuration='" + newTaskDuration + '\'' +
                ", newTaskPriority='" + newTaskPriority + '\'' +
                ", daysToReview=" + daysToReview +
                ", exceptionHandled=" + exceptionHandled +
                ", tempAddress=" + tempAddress +
                '}';
    }

}
