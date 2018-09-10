package com.kairos.dto.user.country.basic_details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author pradeep
 * @date - 11/4/18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryDTO {

    private Long id;
    private String name;
    private Long currencyId;

    public CountryDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }


    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CountryDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
