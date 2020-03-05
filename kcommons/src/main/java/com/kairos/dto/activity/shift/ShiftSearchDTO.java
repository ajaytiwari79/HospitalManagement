package com.kairos.dto.activity.shift;

import com.kairos.dto.gdpr.FilterSelectionDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ShiftSearchDTO {
    private String searchText;
    private List<FilterSelectionDTO> filtersData;
}

