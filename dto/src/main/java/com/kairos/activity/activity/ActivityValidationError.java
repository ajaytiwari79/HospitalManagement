package com.kairos.activity.activity;/*
 *Created By Pavan on 11/8/18
 *
 */

import com.google.common.base.Strings;

import javax.validation.constraints.NotEmpty;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

public class ActivityValidationError {
    private BigInteger id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> messages;

    public ActivityValidationError() {
        //Default Constructor
    }



    public ActivityValidationError(BigInteger id, String name, LocalDate startDate, LocalDate endDate, List<String> messages) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.messages = messages;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }
}
