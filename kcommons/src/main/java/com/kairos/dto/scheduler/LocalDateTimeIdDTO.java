package com.kairos.dto.scheduler;


import java.time.LocalDateTime;
import java.math.BigInteger;
public class LocalDateTimeIdDTO {
    private LocalDateTime dateTime;
    private BigInteger id;


    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

}

