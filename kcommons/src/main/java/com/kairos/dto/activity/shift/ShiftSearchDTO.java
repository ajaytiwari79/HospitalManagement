package com.kairos.dto.activity.shift;

import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.enums.data_filters.StaffFilterSelectionDTO;
import com.kairos.enums.shift.ShiftFilterDurationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ShiftSearchDTO {
    private Long loggedInUserId;
    private String searchText;
    private Long selectedStaffId;
    private Date startDate;
    private Date endDate;
    private boolean multiStaff;
    private ShiftFilterDurationType shiftFilterDurationType = ShiftFilterDurationType.INDIVIDUAL;
    private List<StaffFilterSelectionDTO> staffFilters;
    private List<FilterSelectionDTO> filtersData;

}
