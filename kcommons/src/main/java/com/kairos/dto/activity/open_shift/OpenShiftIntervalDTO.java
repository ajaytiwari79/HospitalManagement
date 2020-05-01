package com.kairos.dto.activity.open_shift;

import com.kairos.enums.DurationType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import java.math.BigInteger;

@Getter
@Setter
public class OpenShiftIntervalDTO{
    private BigInteger id;
    private int from;
    private int to;
    private Long countryId;
    private DurationType type;

    @AssertTrue(message = "from can't be less than to")
    public boolean isValid() {
        return this.to>this.from;
    }

}
