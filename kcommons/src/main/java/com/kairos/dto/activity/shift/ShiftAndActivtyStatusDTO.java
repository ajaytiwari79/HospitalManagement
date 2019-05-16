package com.kairos.dto.activity.shift;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ShiftAndActivtyStatusDTO {
  List<ShiftDTO> shifts;
  List<ShiftActivityResponseDTO> shiftActivityStatusResponse;

    public ShiftAndActivtyStatusDTO() {
    }

    public ShiftAndActivtyStatusDTO(List<ShiftDTO> shifts, List<ShiftActivityResponseDTO> shiftActivityStatusResponse) {
        this.shifts = shifts;
        this.shiftActivityStatusResponse = shiftActivityStatusResponse;
    }
}
