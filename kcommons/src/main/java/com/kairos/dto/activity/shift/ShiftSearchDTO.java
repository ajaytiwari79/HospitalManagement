package com.kairos.dto.activity.shift;

import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.enums.data_filters.StaffFilterSelectionDTO;
import com.kairos.enums.shift.ShiftFilterDurationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private Set<Long> staffIds;
    private Long staffFavouriteFiltersId;
    private Long staffId;

    public Set<Long> getStaffIds() {
        this.staffIds= ObjectUtils.isNullOrElse(this.staffIds,new HashSet<>());
        return staffIds;
    }
}
