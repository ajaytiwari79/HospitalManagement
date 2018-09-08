package com.kairos.dto.user.country.agreement.cta.cta_response;

public class TimeTypeResponseDTO {
    private Long id;
    private String name;
    private  String label;

    public TimeTypeResponseDTO() {
        //default constructor
    }

    public TimeTypeResponseDTO(Long id, String name) {
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
