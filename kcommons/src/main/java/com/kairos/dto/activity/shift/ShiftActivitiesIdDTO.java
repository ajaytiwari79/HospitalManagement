package com.kairos.dto.activity.shift;

import lombok.*;

import java.math.BigInteger;
import java.util.List;

/**
 * @author pradeep
 * @date - 19/9/18
 */
@Getter
@Setter
@NoArgsConstructor
public class ShiftActivitiesIdDTO {

    private BigInteger shiftId;
    private List<BigInteger> activityIds;

    public ShiftActivitiesIdDTO(BigInteger shiftId, List<BigInteger> activityIds) {
        this.shiftId = shiftId;
        this.activityIds = activityIds;
    }
}
