package com.kairos.dto.activity.shift;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ShiftAndActivtyStatusDTO {
  List<ShiftDTO> shiftDTOList;
  List<ShiftActivityResponseDTO> shiftActivityResponseDTOS;

    public ShiftAndActivtyStatusDTO() {
    }

    public ShiftAndActivtyStatusDTO(List<ShiftDTO> shiftDTOList, List<ShiftActivityResponseDTO> shiftActivityResponseDTOS) {
        this.shiftDTOList = shiftDTOList;
        this.shiftActivityResponseDTOS = shiftActivityResponseDTOS;
    }
}
