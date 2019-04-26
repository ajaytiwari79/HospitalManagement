package com.kairos.persistence.model.staff;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Created by prabjot on 24/1/18.
 */
public class TimeCareStaffDTO {

    @JacksonXmlProperty
    private String Id;
    @JacksonXmlProperty
    private String FirstName;
    @JacksonXmlProperty
    private String LastName;
    @JacksonXmlProperty
    private String Gender;
    @JacksonXmlProperty
    private String Address;
    @JacksonXmlProperty
    private String ZipCode;
    @JacksonXmlProperty
    private String City;
    @JacksonXmlProperty
    private String TelephoneNumber;
    @JacksonXmlProperty
    private String CellPhoneNumber;
    @JacksonXmlProperty
    private String Email;
    @JacksonXmlProperty
    private String BirthDate;
    @JacksonXmlProperty
    private Integer Age;
    @JacksonXmlProperty
    private Integer LanguageId;
    @JacksonXmlProperty
    private String ParentWorkPlaceId;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getZipCode() {
        return ZipCode;
    }

    public void setZipCode(String zipCode) {
        ZipCode = zipCode;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getTelephoneNumber() {
        return TelephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        TelephoneNumber = telephoneNumber;
    }

    public String getCellPhoneNumber() {
        return CellPhoneNumber;
    }

    public void setCellPhoneNumber(String cellPhoneNumber) {
        CellPhoneNumber = cellPhoneNumber;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getBirthDate() {
        return BirthDate;
    }

    public void setBirthDate(String birthDate) {
        BirthDate = birthDate;
    }

    public Integer getAge() {
        return Age;
    }

    public void setAge(Integer age) {
        Age = age;
    }

    public Integer getLanguageId() {
        return LanguageId;
    }

    public void setLanguageId(Integer languageId) {
        LanguageId = languageId;
    }

    public String getParentWorkPlaceId() {
        return ParentWorkPlaceId;
    }

    public void setParentWorkPlaceId(String parentWorkPlaceId) {
        ParentWorkPlaceId = parentWorkPlaceId;
    }
}
