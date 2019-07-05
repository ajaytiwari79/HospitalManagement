package com.kairos.dto.user.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNullOrElse;

/**
 * Created by Jasgeet on 13/10/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class StaffFilterDTO {
    private String moduleId;
    private String filterJson;
    private List<FilterSelectionDTO> filtersData;
    private long id;
    private String searchText;
    private String name;
    private List<Long> staffIds;

    public void setFiltersData(List<FilterSelectionDTO> filtersData) {
        this.filtersData = isNullOrElse(filtersData,new ArrayList<>());
    }

    public boolean isValidFilterForShift(){
        boolean isValidFilterForShift = false;
        for (FilterSelectionDTO filterSelectionDTO : this.getFiltersData()) {
            isValidFilterForShift = isCollectionNotEmpty(filterSelectionDTO.getValue());
            if(isValidFilterForShift){
                break;
            }
        }
        return isValidFilterForShift;
    }
}

