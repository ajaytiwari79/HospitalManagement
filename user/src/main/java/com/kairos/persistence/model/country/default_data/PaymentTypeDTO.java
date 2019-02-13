package com.kairos.persistence.model.country.default_data;

import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.NotBlank;

@QueryResult
public class PaymentTypeDTO {

    private Long id;
    @NotBlank(message = "error.PaymentType.name.notEmpty")
    private String name;
    private String description;

    public PaymentTypeDTO() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
