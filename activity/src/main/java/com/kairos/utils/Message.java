package com.kairos.utils;

import java.util.Date;

public class Message {
    private Date currentDate;
    private int id;

    public Message() {
    }

    public Message(Date currentDate, int id) {
        this.currentDate = currentDate;
        this.id = id;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
