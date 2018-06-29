package com.kairos.planning.domain;

import java.util.LinkedList;

public class Route {


    private Long id;
    private LinkedList<TaskOrEmployee> checkpoints= new LinkedList<>();
    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    private Vehicle vehicle;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    private Employee employee;

    public Route(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LinkedList<TaskOrEmployee> getCheckpoints() {
        return checkpoints;
    }
    public void addCheckpoint(TaskOrEmployee taskOrEmployee){
        checkpoints.add(taskOrEmployee);
    }
}
