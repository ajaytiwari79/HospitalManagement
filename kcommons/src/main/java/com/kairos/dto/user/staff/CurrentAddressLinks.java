package com.kairos.dto.user.staff;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by oodles on 19/4/17.
 */
@Getter
@Setter
public class CurrentAddressLinks {
    private SearchPostalDistrict searchPostalDistrict;

    private AvailableCountries availableCountries;

    @Override
    public String toString()
    {
        return "ClassPojo [searchPostalDistrict = "+searchPostalDistrict+", availableCountries = "+availableCountries+"]";
    }
}
