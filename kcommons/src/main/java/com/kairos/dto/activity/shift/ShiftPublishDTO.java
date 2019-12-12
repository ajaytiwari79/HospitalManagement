package com.kairos.dto.activity.shift;

import com.kairos.enums.shift.ShiftStatus;
import lombok.*;

import java.util.List;

/**
 * Created by vipul on 9/5/18.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShiftPublishDTO {
    private List<ShiftActivitiesIdDTO> shifts;
    private ShiftStatus status;
    private String comment;
}
