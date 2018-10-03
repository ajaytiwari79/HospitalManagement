package com.kairos.dto.scheduler.scheduler_panel;


import java.time.LocalDateTime;
import java.math.BigInteger;
public class LocalDateTimeIdDTO {
    private LocalDateTime dateTime;
    private BigInteger id;

    public LocalDateTimeIdDTO() {
    }

    public LocalDateTimeIdDTO(BigInteger id, LocalDateTime dateTime) {
        this.dateTime = dateTime;
        this.id = id;
    }

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

