package com.kairos.dto.activity.phase;

import java.math.BigInteger;

/**
 * Created by vipul on 26/9/17.
 */
public class PhaseWeeklyDTO {
    private BigInteger id;

    private String name;
    private String description;
    private int duration;
    private int sequence;
    private Long organizationId;
    private int weekCount;
    private int year;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }


    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(long organizationId) {
        this.organizationId = organizationId;
    }

    public int getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(int weekCount) {
        this.weekCount = weekCount;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public PhaseWeeklyDTO(BigInteger id, String name, String description, int duration, int sequence,  Long organizationId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.sequence = sequence;
        this.organizationId = organizationId;
    }

    public PhaseWeeklyDTO() {
    }
}
