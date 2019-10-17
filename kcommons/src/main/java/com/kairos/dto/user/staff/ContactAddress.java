package com.kairos.dto.user.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;


@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ContactAddress{

    private String street1;

    private int floorNumber;

    private String houseNumber;

    private String city;

    private String regionCode;

    private String regionName;

    private String province;

    private float longitude;

    private float latitude;

    private long startDate;

    private long endDate;

    private String country;

    private boolean privateAddress;

    private boolean isVerifiedByVisitour;

    public boolean isVerifiedByVisitour() {
        return isVerifiedByVisitour;
    }

    public boolean isAddressProtected() {
        return isAddressProtected;
    }

    private boolean isAddressProtected;

    private String streetUrl;

    private boolean isEnabled = true;

    private String contactPersonForBillingAddress;
    private Long zipCode;

    private String description;


}
