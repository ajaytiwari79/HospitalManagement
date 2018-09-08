package com.kairos.dto.activity.wta;

/**
 * Created by pavan on 24/4/18.
 */
public class AgeRange{
    private int from;
    private int to;
    private int leavesAllowed;

    public AgeRange() {
        //Default Constructor
    }

    public AgeRange(int from, int to, int leavesAllowed) {
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
}
