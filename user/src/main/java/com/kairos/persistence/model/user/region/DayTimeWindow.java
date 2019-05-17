package com.kairos.persistence.model.user.region;

import com.kairos.config.neo4j.converter.LocalTimeConverter;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * @author pradeep
 * @date - 11/6/18
 */
@NodeEntity
public class DayTimeWindow extends UserBaseEntity {

    private DayOfWeek dayOfWeek;
    @Convert(LocalTimeConverter.class)
    private LocalTime fromTime;
    @Convert(LocalTimeConverter.class)
    private LocalTime toTime;

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
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
