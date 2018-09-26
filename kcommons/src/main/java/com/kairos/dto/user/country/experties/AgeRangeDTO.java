package com.kairos.dto.user.country.experties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * Created by pavan on 26/4/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgeRangeDTO implements Comparable<AgeRangeDTO>,Serializable{
    private Long id;
    private int from;
    private Integer to;
    private Integer leavesAllowed;

    public AgeRangeDTO() {
        //Default Constructor
    }

    public AgeRangeDTO(Long id, int from, Integer to, Integer leavesAllowed) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.leavesAllowed = leavesAllowed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
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
    public int compareTo(AgeRangeDTO o) {
        return this.from-o.from;
    }

}
