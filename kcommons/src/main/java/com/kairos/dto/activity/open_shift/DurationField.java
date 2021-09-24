package com.kairos.dto.activity.open_shift;

import com.kairos.enums.DurationType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DurationField  {
    private Integer value;
    private DurationType type;
}
