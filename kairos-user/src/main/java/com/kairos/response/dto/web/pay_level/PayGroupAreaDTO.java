package com.kairos.response.dto.web.pay_level;

import javax.validation.constraints.NotNull;

/**
 * Created by prabjot on 21/12/17.
 */
public class PayGroupAreaDTO {

    @NotNull(message = "Name can not be null")
    private String name;
    private String description;
    private Long id;

    public PayGroupAreaDTO() {
        //default constructor
    }

    public PayGroupAreaDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
