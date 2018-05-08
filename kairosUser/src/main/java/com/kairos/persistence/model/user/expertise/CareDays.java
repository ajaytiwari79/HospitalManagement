package com.kairos.persistence.model.user.expertise;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class CareDays extends UserBaseEntity implements Comparable<CareDays> {
    private int from;
    private int to;
    private int leavesAllowed;

    public CareDays() {
        //Default Constructor
    }

    public CareDays(int from, int to, int leavesAllowed) {
        this.from = from;
        this.to = to;
        this.leavesAllowed = leavesAllowed;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getLeavesAllowed() {
        return leavesAllowed;
    }

    public void setLeavesAllowed(int leavesAllowed) {
        this.leavesAllowed = leavesAllowed;
    }

    @Override
    public int compareTo(CareDays o) {
        return o.from-this.from;
    }
}
