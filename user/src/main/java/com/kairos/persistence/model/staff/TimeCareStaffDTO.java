package com.kairos.persistence.model.staff;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by prabjot on 24/1/18.
 */
@Getter
@Setter
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
}
