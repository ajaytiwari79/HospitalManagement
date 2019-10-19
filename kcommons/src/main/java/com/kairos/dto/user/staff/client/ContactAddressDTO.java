package com.kairos.dto.user.staff.client;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created by pavan on 27/2/18.
 */
@Getter
@Setter
@NoArgsConstructor
public class ContactAddressDTO {
    @NotBlank(message = "message.houseNumber.null")
    private String houseNumber;
    private Integer floorNumber;
    @NotBlank(message = "message.street.null")
    private String street;
    @NotNull(message = "message.zipCodeId.null")
    private Long zipCodeId;
    @NotBlank(message = "message.city.null")
    private String city;
    @NotNull(message = "message.municipality.null")
    private Long municipalityId;
    @NotBlank(message = "message.region.null")
    private String regionName;
    private String country;
    private Float latitude;
    private Float longitude;
    @NotBlank(message = "message.province.null")
    private String province;
    private String streetUrl;
    private Boolean addressProtected;
    private Boolean verifiedByVisitour;
    private Integer zipCodeValue;
    private String municipalityName;

    public ContactAddressDTO(String houseNumber, Integer floorNumber, String street, String city, String regionName, String country, Float latitude, Float longitude,
                             String province, String streetUrl, Boolean addressProtected, Boolean verifiedByVisitour) {
        this.houseNumber = houseNumber;
        this.floorNumber = floorNumber;
        this.street = street;
        this.city = city;
        this.regionName = regionName;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.province = province;
        this.streetUrl = streetUrl;
        this.addressProtected = addressProtected;
        this.verifiedByVisitour = verifiedByVisitour;
    }


    public void setMunicipalityName(String municipalityName) {
        this.municipalityName = StringUtils.trim(municipalityName);
    }
}


