package com.planner.responseDto.locationDto;

public class OptaLocationDTO {
    private String country;
    private Integer zip;
    private String city;
    private String district;
    private String street;
    private String houseNumber;
    private Double longitude;
    private Double latitude;
    private Long optaPlannerId;
    private Boolean isAddressVerified;

    public Boolean getAddressVerified() {
        return isAddressVerified;
    }

    public void setAddressVerified(Boolean addressVerified) {
        isAddressVerified = addressVerified;
    }

    public Long getOptaPlannerId() {
        return optaPlannerId;
    }

    public void setOptaPlannerId(Long optaPlannerId) {
        this.optaPlannerId = optaPlannerId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getZip() {
        return zip;
    }

    public void setZip(Integer zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getAddress(){
        return houseNumber+","+street+","+city+","+country;
    }

    @Override
    public String toString() {
        return "OptaLocationDTO{" +
                "basic_details='" + country + '\'' +
                ", zip=" + zip +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", street='" + street + '\'' +
                ", houseNumber='" + houseNumber + '\'' +
                '}';
    }
}
