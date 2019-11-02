package com.kairos.dto.user.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.organization.address.ZipCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ClientTemporaryAddress {
    private Long id;
    private String street1;

    private int floorNumber;

    private String houseNumber;

    private String city;

    private String regionCode;

    private String regionName;

    private String province;


    @NotNull(message = "error.ContactAddress.longitude.notnull")
    private float longitude;

    @NotNull(message = "error.ContactAddress.latitude.notnull")
    private float latitude;

    private long startDate;

    private long endDate;

    private String country;

    private boolean privateAddress;

    private boolean isVerifiedByVisitour;

    private boolean isAddressProtected;

    private String streetUrl;

    private boolean isEnabled = true;

    private String contactPersonForBillingAddress;

    private ZipCode zipCode;


    private String description;

    private String locationName;

}
