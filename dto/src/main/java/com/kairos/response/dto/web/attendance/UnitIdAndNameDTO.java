package com.kairos.response.dto.web.attendance;

public class UnitIdAndNameDTO {
    private Long id;
    private String name;

    public UnitIdAndNameDTO() {
    }

    public UnitIdAndNameDTO(Long id, String name) {
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
