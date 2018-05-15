package com.kairos.persistence.model.user.expertise;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class CareDays extends UserBaseEntity implements Comparable<CareDays> {
    private Integer from;
    private Integer to;
    private Integer leavesAllowed;

    public CareDays() {
        //Default Constructor
    }

    public CareDays(int from, int to, int leavesAllowed) {
        this.from = from;
        this.to = to;
        this.leavesAllowed = leavesAllowed;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public Integer getLeavesAllowed() {
        return leavesAllowed;
    }

    public void setLeavesAllowed(Integer leavesAllowed) {
        this.leavesAllowed = leavesAllowed;
    }

    @Override
    public int compareTo(CareDays o) {
        return o.from-this.from;
    }
}
