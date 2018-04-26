package com.kairos.response.dto.web.experties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import java.util.Optional;

/**
 * Created by pavan on 25/4/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Valid
public class SeniorDaysValueDTO implements Comparable<SeniorDaysValueDTO>{
    private int from;
    private int to;
    private int leavesAllowed;

    public SeniorDaysValueDTO() {
        //Default Constructor
    }

    public SeniorDaysValueDTO(int from, int to, int leavesAllowed) {
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
    @AssertTrue(message = "Incorrect Data")
    public boolean isValid() {

        if (!Optional.ofNullable(this.from).isPresent()) {
            return false;
        }
        if (Optional.ofNullable(this.to).isPresent()) {
            if (this.to < this.from)
                return false;
        }

        return true;
    }

    @Override
    public int compareTo(SeniorDaysValueDTO o) {
        return o.from-this.from;
    }
}
