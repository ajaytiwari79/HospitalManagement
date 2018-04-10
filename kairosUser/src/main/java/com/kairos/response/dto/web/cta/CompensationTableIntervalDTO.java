package com.kairos.response.dto.web.cta;

import com.kairos.config.neo4j.converter.LocalTimeStringConverter;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import java.time.LocalTime;

/**
 * Created by prerna on 20/12/17.
 */
public class CompensationTableIntervalDTO {

    @Convert(LocalTimeStringConverter.class)
    private LocalTime from;
    @Convert(LocalTimeStringConverter.class)
    private LocalTime to;
    private float value;

    public LocalTime getFrom() {
        return from;
    }

    public void setFrom(LocalTime from) {
        this.from = from;
    }

    public LocalTime getTo() {
        return to;
    }

    public void setTo(LocalTime to) {
        this.to = to;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
