package com.kairos.dto.user.organization.address;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by prabjot on 19/1/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class AddressDTO {
    private Long id;
    private String streetUrl;
    private String street1;
    private int floorNumber;
    private String houseNumber;
    private String billingPerson;
    private String city;
    private String municipalityName;
    private float longitude;
    private float latitude;
    private Long zipCode;

    private Long typeOfHouseId;

    private long paymentTypeId;
    private long currencyId;
    private String country;
    private String regionName;
    private String province;
    private boolean isVerifiedByGoogleMap;
    private boolean addressProtected;
    private boolean verifiedByVisitour;
    private long startDate;
    private long endDate;
    private String description;
    private Long municipalityId;

}
