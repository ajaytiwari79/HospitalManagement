package com.kairos.dto.activity.shift;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShiftAndActivtyStatusDTO {
  private List<ShiftDTO> shifts;
  private List<ShiftActivityResponseDTO> shiftActivityStatusResponse;
}
