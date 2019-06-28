package com.kairos.dto.user.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import lombok.*;

import java.util.List;

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

    public void setFiltersData(List<FilterSelectionDTO> filtersData) {
        this.filtersData = filtersData;
    }
}

