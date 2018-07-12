package com.kairos.response.dto.web.attendance;

import java.time.LocalDateTime;

public class AttendanceDuration {


    private LocalDateTime from;
    private LocalDateTime to;

    public AttendanceDuration() {
    }

    public AttendanceDuration(LocalDateTime from) {
        this.from = from;
    }
    public LocalDateTime getFrom() {
        return from;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public void setTo(LocalDateTime to) {
        this.to = to;
    }

}
