package com.kairos.dto.user.country.agreement.cta.cta_response;

public class EmploymentTypeDTO {
    private Long id;
    private String name;

    public EmploymentTypeDTO() {
        //default constructor
    }

    public EmploymentTypeDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
