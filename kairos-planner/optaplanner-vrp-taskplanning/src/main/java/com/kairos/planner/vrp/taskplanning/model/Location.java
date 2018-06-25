package com.kairos.planner.vrp.taskplanning.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Location {
    private Double latitude;
    private Double longitude;
    private int id;
    private long num;

    public Location() {
    }

    public Location(Double latitude, Double longitude, int id, long num) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
        this.num = num;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        return new EqualsBuilder()
                .append(num, location.num)
                .append(latitude, location.latitude)
                .append(longitude, location.longitude)
                .append(id, location.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(latitude)
                .append(longitude)
                .append(id)
                .append(num)
                .toHashCode();
    }

    public String toString(){
        //['NoidaA',56.63576484,9.79802229,9]
        return "["+"'Task"+id+"',"+latitude+","+longitude+","+num+"]";
    }
}
