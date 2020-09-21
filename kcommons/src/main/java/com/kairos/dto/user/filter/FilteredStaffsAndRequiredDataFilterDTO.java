package com.kairos.dto.user.filter;

import com.kairos.wrapper.shift.StaffShiftDetailsDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilteredStaffsAndRequiredDataFilterDTO {
    private List<StaffShiftDetailsDTO> staffShiftDetailsDTOS;
    private RequiredDataForFilterDTO requiredDataForFilterDTO;
}
