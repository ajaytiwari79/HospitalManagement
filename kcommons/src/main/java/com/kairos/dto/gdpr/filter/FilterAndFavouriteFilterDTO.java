package com.kairos.dto.gdpr.filter;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FilterAndFavouriteFilterDTO {


    private List<FilterResponseDTO> allFilters;

    private List<FilterResponseDTO> favouriteFilters;//Might require copied from gdpr
}
