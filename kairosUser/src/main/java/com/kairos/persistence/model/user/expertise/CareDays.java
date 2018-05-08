package com.kairos.persistence.model.user.expertise;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.enums.CareDaysType;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class CareDays extends UserBaseEntity implements Comparable<CareDays> {
    private int from;
    private int to;
    private int leavesAllowed;
    private CareDaysType careDaysType;

    public CareDays() {
        //Default Constructor
    }

    public CareDays(int from, int to, int leavesAllowed, CareDaysType careDaysType) {
        this.from = from;
        this.to = to;
        this.leavesAllowed = leavesAllowed;
        this.careDaysType = careDaysType;
    }

    @Override
    public int compareTo(CareDays o) {
        return o.from-this.from;
    }
}
