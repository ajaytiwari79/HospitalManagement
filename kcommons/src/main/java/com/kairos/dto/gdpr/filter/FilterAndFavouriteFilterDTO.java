package com.kairos.dto.gdpr.filter;

import java.util.List;

public class FilterAndFavouriteFilterDTO {


    private List<FilterResponseDTO> allFilters;

    private List<FilterResponseDTO> favouriteFilters;//Might require copied from gdpr

    public List<FilterResponseDTO> getAllFilters() {
        return allFilters;
    }

    public void setAllFilters(List<FilterResponseDTO> allFilters) {
        this.allFilters = allFilters;
    }

    public List<FilterResponseDTO> getFavouriteFilters() {
        return favouriteFilters;
    }

    public void setFavouriteFilters(List<FilterResponseDTO> favouriteFilters) {
        this.favouriteFilters = favouriteFilters;
    }
}
