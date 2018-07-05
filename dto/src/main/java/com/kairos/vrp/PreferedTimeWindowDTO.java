package com.kairos.vrp;

import java.time.LocalTime;

/**
 * @author pradeep
 * @date - 28/6/18
 */

public class PreferedTimeWindowDTO {


    private String name;
    private Long id;
    private LocalTime fromTime;
    private LocalTime toTime;

    private String timeWindow;


    public String getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow(String timeWindow) {
        this.timeWindow = timeWindow;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalTime getFromTime() {
        return fromTime;
    }

    public void setFromTime(LocalTime fromTime) {
        this.fromTime = fromTime;
    }

    public LocalTime getToTime() {
        return toTime;
    }

    public void setToTime(LocalTime toTime) {
        this.toTime = toTime;
    }
}
