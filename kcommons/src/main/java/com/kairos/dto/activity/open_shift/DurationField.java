package com.kairos.dto.activity.open_shift;

import com.kairos.enums.DurationType;

import java.io.Serializable;

public class DurationField implements Serializable {
    private Integer value;
    private DurationType type;

    public DurationField() {
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
