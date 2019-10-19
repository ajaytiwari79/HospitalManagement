package com.kairos.dto.activity.shift;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author pradeep
 * @date - 14/9/18
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShiftDetailViewDTO {

    private List<ShiftDTO> plannedShifts;
    private List<ShiftDTO> realTimeShifts;
    private List<ShiftDTO> staffValidated;
    private List<ShiftDTO> plannerValidated;

}
