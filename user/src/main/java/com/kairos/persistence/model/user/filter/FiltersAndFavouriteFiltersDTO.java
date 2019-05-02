package com.kairos.persistence.model.user.filter;

import java.util.List;

/**
 * Created by prerna on 30/4/18.
 */
public class FiltersAndFavouriteFiltersDTO {

    private List<FilterQueryResult> allFilters;

    private List<FavoriteFilterQueryResult> favouriteFilters;

    public FiltersAndFavouriteFiltersDTO(){
        // default constructor
    }

    public FiltersAndFavouriteFiltersDTO(List<FilterQueryResult> allFilters, List<FavoriteFilterQueryResult> favouriteFilters){
        this.allFilters = allFilters;
        this.favouriteFilters = favouriteFilters;
    }

    public List<FilterQueryResult> getAllFilters() {
        return allFilters;
    }

    public void setAllFilters(List<FilterQueryResult> allFilters) {
        this.allFilters = allFilters;
    }

    public List<FavoriteFilterQueryResult> getFavouriteFilters() {
        return favouriteFilters;
    }

    public void setFavouriteFilters(List<FavoriteFilterQueryResult> favouriteFilters) {
        this.favouriteFilters = favouriteFilters;
    }
}
