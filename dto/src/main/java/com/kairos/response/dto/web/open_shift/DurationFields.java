package com.kairos.response.dto.web.open_shift;

import com.kairos.persistence.model.enums.DurationType;

public class DurationFields {
    private Integer value;
    private DurationType type;

    public DurationFields() {
        //Default Constructor
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public DurationType getType() {
        return type;
    }

    public void setType(DurationType type) {
        this.type = type;
    }
}
