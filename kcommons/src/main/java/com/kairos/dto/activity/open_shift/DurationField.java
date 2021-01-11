package com.kairos.dto.activity.open_shift;

import com.kairos.enums.DurationType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DurationField implements Serializable {
    private Integer value;
    private DurationType type;
}
