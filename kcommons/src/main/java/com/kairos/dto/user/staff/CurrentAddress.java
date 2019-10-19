package com.kairos.dto.user.staff;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by oodles on 19/4/17.
 */
@Getter
@Setter
public class CurrentAddress {
    private Long postalCode;

    private String restricted;

    private String countryCode;

    private CurrentAddressLinks _links;

    private String postalDistrict;

    private String addressLine5;

    private String administrativeAreaCode;

    private String addressLine4;

    private String addressLine3;

    private GeoCoordinates geoCoordinates;

    private String addressLine2;

    private String addressLine1;


    @Override
    public String toString()
    {
        return "ClassPojo [postalCode = "+postalCode+", restricted = "+restricted+", countryCode = "+countryCode+", postalDistrict = "+postalDistrict+", addressLine5 = "+addressLine5+", administrativeAreaCode = "+administrativeAreaCode+", addressLine4 = "+addressLine4+", addressLine3 = "+addressLine3+", geoCoordinates = "+geoCoordinates+", addressLine2 = "+addressLine2+", addressLine1 = "+addressLine1+"]";
    }
}
