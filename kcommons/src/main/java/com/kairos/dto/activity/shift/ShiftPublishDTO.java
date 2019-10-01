package com.kairos.dto.activity.shift;

import com.kairos.enums.shift.ShiftStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

}
