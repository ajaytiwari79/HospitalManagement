package com.kairos.persistence.model.staffing_level;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * Created by prabjot on 12/1/18.
 */
public class TimeCareStaffingLevelDTO {

    @JacksonXmlProperty
    private Integer Id;
    @JacksonXmlProperty
    private Integer LineType;
    @JacksonXmlProperty
    private Integer WorkPlaceId;
    @JacksonXmlProperty
    private List<TimeCareStaffingLevelIntervalDTO> arrayOfValue;

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public Integer getLineType() {
        return LineType;
    }

    public void setLineType(Integer lineType) {
        LineType = lineType;
    }

    public Integer getWorkPlaceId() {
        return WorkPlaceId;
    }

    public void setWorkPlaceId(Integer workPlaceId) {
        WorkPlaceId = workPlaceId;
    }

    public List<TimeCareStaffingLevelIntervalDTO> getArrayOfValue() {
        return arrayOfValue;
    }

    public void setArrayOfValue(List<TimeCareStaffingLevelIntervalDTO> arrayOfValue) {
        this.arrayOfValue = arrayOfValue;
    }

    @Override
    public String toString() {
        return "TimeCareStaffingLevelDTO{" +
                "Id=" + Id +
                ", LineType=" + LineType +
                ", WorkPlaceId=" + WorkPlaceId +
                ", arrayOfValue=" + arrayOfValue +
                '}';
    }
}
