package com.kairos.dto.user.organization;

import com.kairos.dto.user.organization.address.ZipCode;

public class ZipCodeDTO {

    private String name;

    private Long id;
    private int zipCode;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    private ZipCodeDTO() {
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }
}
