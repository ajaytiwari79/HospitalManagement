package com.kairos.activity.web;

import com.kairos.user.country.basic_details.AvailableCountries;

/**
 * Created by oodles on 19/4/17.
 */
public class CurrentAddressLinks {
    private SearchPostalDistrict searchPostalDistrict;

    private AvailableCountries availableCountries;

    public SearchPostalDistrict getSearchPostalDistrict ()
    {
        return searchPostalDistrict;
    }

    public void setSearchPostalDistrict (SearchPostalDistrict searchPostalDistrict)
    {
        this.searchPostalDistrict = searchPostalDistrict;
    }

    public AvailableCountries getAvailableCountries ()
    {
        return availableCountries;
    }

    public void setAvailableCountries (AvailableCountries availableCountries)
    {
        this.availableCountries = availableCountries;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [searchPostalDistrict = "+searchPostalDistrict+", availableCountries = "+availableCountries+"]";
    }
}
