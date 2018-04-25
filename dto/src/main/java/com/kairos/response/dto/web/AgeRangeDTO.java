package com.kairos.response.dto.web;

/**
 * Created by pavan on 25/4/18.
 */
public class AgeRangeDTO {
    private int from;
    private int to;
    private int leavesAllowed;

    public AgeRangeDTO() {
        //Default Constructor
    }

    public AgeRangeDTO(int from, int to, int leavesAllowed) {
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
