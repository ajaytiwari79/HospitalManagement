package com.kairos.activity.staffing_level;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.Date;

/**
 * Created by prabjot on 12/1/18.
 */
public class TimeCareStaffingLevelIntervalDTO {

    @JacksonXmlProperty
    private Date fromDate;
    @JacksonXmlProperty
    private Date toDate;
    @JacksonXmlProperty
    private Integer value;
    @JacksonXmlProperty
    private Integer WorkPlaceId;

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getWorkPlaceId() {
        return WorkPlaceId;
    }

    public void setWorkPlaceId(Integer workPlaceId) {
        WorkPlaceId = workPlaceId;
    }

    @Override
    public String toString() {
        return "TimeCareStaffingLevelIntervalDTO{" +
                "fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", value=" + value +
                ", WorkPlaceId=" + WorkPlaceId +
                '}';
    }
}
