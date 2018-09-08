package com.kairos.user.staff;

/**
 * Created by oodles on 19/4/17.
 */
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

    public Long getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(Long postalCode) {
        this.postalCode = postalCode;
    }

    public String getRestricted ()
    {
        return restricted;
    }

    public void setRestricted (String restricted)
    {
        this.restricted = restricted;
    }

    public String getCountryCode ()
    {
        return countryCode;
    }

    public void setCountryCode (String countryCode)
    {
        this.countryCode = countryCode;
    }

    public String getAddressLine5() {
        return addressLine5;
    }

    public void setAddressLine5(String addressLine5) {
        this.addressLine5 = addressLine5;
    }

    public String getAdministrativeAreaCode() {
        return administrativeAreaCode;
    }

    public void setAdministrativeAreaCode(String administrativeAreaCode) {
        this.administrativeAreaCode = administrativeAreaCode;
    }

    public String getAddressLine4() {
        return addressLine4;
    }

    public void setAddressLine4(String addressLine4) {
        this.addressLine4 = addressLine4;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getPostalDistrict ()
    {
        return postalDistrict;
    }

    public void setPostalDistrict (String postalDistrict)
    {
        this.postalDistrict = postalDistrict;
    }


    public CurrentAddress() {
    }

    public GeoCoordinates getGeoCoordinates ()
    {
        return geoCoordinates;
    }

    public void setGeoCoordinates (GeoCoordinates geoCoordinates)
    {
        this.geoCoordinates = geoCoordinates;
    }



    public String getAddressLine1 ()
    {
        return addressLine1;
    }

    public void setAddressLine1 (String addressLine1)
    {
        this.addressLine1 = addressLine1;
    }

    public CurrentAddressLinks get_links() {
        return _links;
    }

    public void set_links(CurrentAddressLinks _links) {
        this._links = _links;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [postalCode = "+postalCode+", restricted = "+restricted+", countryCode = "+countryCode+", postalDistrict = "+postalDistrict+", addressLine5 = "+addressLine5+", administrativeAreaCode = "+administrativeAreaCode+", addressLine4 = "+addressLine4+", addressLine3 = "+addressLine3+", geoCoordinates = "+geoCoordinates+", addressLine2 = "+addressLine2+", addressLine1 = "+addressLine1+"]";
    }
}
