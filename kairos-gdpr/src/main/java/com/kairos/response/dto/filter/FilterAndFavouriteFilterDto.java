package com.kairos.response.dto.filter;

import java.util.List;

public class FilterAndFavouriteFilterDto {


    private List<FilterResponseDto> allFilters;

    private List<FilterResponseDto> favouriteFilters;

    public List<FilterResponseDto> getAllFilters() {
        return allFilters;
    }

    public void setAllFilters(List<FilterResponseDto> allFilters) {
        this.allFilters = allFilters;
    }

    public List<FilterResponseDto> getFavouriteFilters() {
        return favouriteFilters;
    }

    public void setFavouriteFilters(List<FilterResponseDto> favouriteFilters) {
        this.favouriteFilters = favouriteFilters;
    }
}
