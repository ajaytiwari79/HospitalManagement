package com.kairos.persistence.model.organization;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by oodles on 16/11/16.
 */
@NodeEntity
public class OpeningHours extends UserBaseEntity {


    private DayType.DayTypeEnum day;
    private String timing;
    private int index;

    public DayType.DayTypeEnum getDay() {
        return day;
    }

    public void setDay(DayType.DayTypeEnum day) {
        this.day = day;
    }

    public String getTiming() {
        return timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }



    public OpeningHours(DayType.DayTypeEnum day, String timing,int index) {
        this.day = day;
        this.timing = timing;
        this.index = index;

    }

    public OpeningHours() {
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
