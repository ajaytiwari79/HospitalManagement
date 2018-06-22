package com.kairos.planner.vrp.taskplanning.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@PlanningEntity
public class Shift extends TaskOrShift{
    private String id;
    private Employee employee;
    private LocalDate localDate;

    private LocalDateTime startTime;
    @CustomShadowVariable(variableListenerClass = ShiftEndTimeListener.class,
            sources = @PlanningVariableReference(entityClass = Task.class,variableName = "nextTask"))
    private LocalDateTime endTime;


    public Shift(String id, Employee employee, LocalDate localDate, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.employee = employee;
        this.localDate = localDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Shift() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public LocalDateTime getStartTime() {
        return localDate.atStartOfDay().with(getDefaultShiftStart());
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public static LocalTime getDefaultShiftStart(){
        return LocalTime.of(7,0);
    }
    public int getNumberOfTasks(){
        int i=0;
        Task task=getNextTask();
        while (task!=null){
            i++;
            task=task.getNextTask();
        }
        return i;
    }

    public int getTotalPlannedMinutesFromChain(){
        int d=0;
        Task task=getNextTask();
        while (task!=null){
            d+=task.getPlannedDuration()+task.getDrivingTime();
            task=task.getNextTask();
        }
        return d;
    }
    public int getTotalPlannedMinutes(){
        if(endTime==null)return 0;
        return (int)ChronoUnit.MINUTES.between(getStartTime(), endTime);
    }

    public boolean isHalfWorkDay(){
        return localDate.getDayOfWeek().getValue()==5;
    }
    public boolean isFullWorkDay(){
        return localDate.getDayOfWeek().getValue()<5;
    }
    @Override
    public String toString() {
        return "Shift{" +id+
                ":" + employee.getName() +
                ":" + localDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Shift shift = (Shift) o;

        return new EqualsBuilder()
                .append(id, shift.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }

    public String getTaskChainString(){
        StringBuilder sb = new StringBuilder();
        Task temp=getNextTask();
        int duration=0;
        while (temp!=null){
            sb.append("[("+temp.isEmployeeEligible()+":"+temp.getDrivingTime()+")"+temp.toString()+"]->");
            duration+= temp.getDrivingTime()+temp.getPlannedDuration();
            temp=temp.getNextTask();
        }
        return "TotalDur("+duration+"):Drive("+getChainDrivingTime()+")("+getStartTime()+":"+endTime+")"+sb.toString();
    }

    public String getLocationsString(){
        StringBuilder sb = new StringBuilder();
        Task temp=getNextTask();
        while (temp!=null){
            sb.append(temp.getLatLongString()+" -> ");
            temp=temp.getNextTask();
        }
        return sb.toString();
    }


    public int getChainDuration() {
        int duration=0;
        Task temp=getNextTask();
        while (temp!=null){
            duration+=temp.getDrivingTime()+temp.getPlannedDuration();
            temp=temp.getNextTask();
        }
        return duration;
    }
    public int getChainDrivingTime() {
        int duration=0;
        Task temp=getNextTask();
        while (temp!=null){
            duration+=temp.getDrivingTime();
            temp=temp.getNextTask();
        }
        return duration;
    }
    public List<Task> getTaskList(){
        List<Task> tasks= new ArrayList<>();
        Task temp=getNextTask();
        while (temp!=null){
            tasks.add(temp);
            temp=temp.getNextTask();
        }
        return tasks;
    }
}
