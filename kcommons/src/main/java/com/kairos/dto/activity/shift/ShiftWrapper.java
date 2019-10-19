package com.kairos.dto.activity.shift;

import com.kairos.dto.activity.open_shift.OpenShiftResponseDTO;
import com.kairos.dto.user.staff.staff.StaffAccessRoleDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Created by vipul on 11/5/18.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShiftWrapper {
    private List<ShiftDTO> assignedShifts;
    private List<OpenShiftResponseDTO> openShifts;
    private StaffAccessRoleDTO staffDetails;
    private ButtonConfig buttonConfig;
    private Map<LocalDate,List<FunctionDTO>> assignedFunctionsObj;

}
