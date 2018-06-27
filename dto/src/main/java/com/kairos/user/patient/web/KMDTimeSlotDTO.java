package com.kairos.user.patient.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Jasgeet on 26/9/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class KMDTimeSlotDTO {

    private Long id;
    private String title;
    private String start;
    private String end;
    private String category;
    private boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
