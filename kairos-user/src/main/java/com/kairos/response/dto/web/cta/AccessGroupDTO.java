package com.kairos.response.dto.web.cta;

public class AccessGroupDTO {
    private Long id;
    private String name;

    public AccessGroupDTO() {
        //default constructor
    }

    public AccessGroupDTO(Long id, String name) {
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
