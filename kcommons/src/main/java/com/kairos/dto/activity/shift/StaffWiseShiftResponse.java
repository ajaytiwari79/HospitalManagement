package com.kairos.dto.activity.shift;

import com.kairos.dto.user.staff.staff.Staff;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffWiseShiftResponse {
    private Staff staff;
    private List<ShiftResponse> shiftResponses;

}
