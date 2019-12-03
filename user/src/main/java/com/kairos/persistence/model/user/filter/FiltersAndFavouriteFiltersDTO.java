package com.kairos.persistence.model.user.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by prerna on 30/4/18.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FiltersAndFavouriteFiltersDTO {

    private List<FilterQueryResult> allFilters;

    private List<FavoriteFilterQueryResult> favouriteFilters;


}
