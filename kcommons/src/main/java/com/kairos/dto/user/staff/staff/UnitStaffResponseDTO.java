package com.kairos.dto.user.staff.staff;


import com.kairos.dto.user.staff.StaffDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public class UnitStaffResponseDTO {

    private Long unitId;
    private List<StaffDTO> staffList = new ArrayList<>();
}
