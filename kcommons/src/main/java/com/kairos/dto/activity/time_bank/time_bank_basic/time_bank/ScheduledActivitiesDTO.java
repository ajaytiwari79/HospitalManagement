package com.kairos.dto.activity.time_bank.time_bank_basic.time_bank;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 20/8/18
 */

public class ScheduledActivitiesDTO {

    private BigInteger id;
    private String name;
    private long scheduledMinutes;

    public ScheduledActivitiesDTO(BigInteger id, String name, long scheduledMinutes) {
        this.id = id;
        this.name = name;
        this.scheduledMinutes = scheduledMinutes;
    }

    public ScheduledActivitiesDTO() {
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

    public long getScheduledMinutes() {
        return scheduledMinutes;
    }

    public void setScheduledMinutes(long scheduledMinutes) {
        this.scheduledMinutes = scheduledMinutes;
    }
}
