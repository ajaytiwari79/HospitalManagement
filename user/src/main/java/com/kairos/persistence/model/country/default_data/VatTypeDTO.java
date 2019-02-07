package com.kairos.persistence.model.country.default_data;

import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.NotBlank;

@QueryResult
public class VatTypeDTO {

    private Long id;
    @NotBlank(message = "error.VatType.name.notEmpty")
    private String name;
    private int code;
    private String description;
    @NotBlank(message = "error.VatType.percentage.notEmpty")
    private String percentage;

    public VatTypeDTO() {
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

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public VatTypeDTO(@NotBlank(message = "error.VatType.name.notEmpty") String name, int code, String description, @NotBlank(message = "error.VatType.percentage.notEmpty") String percentage) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.percentage = percentage;
    }
}
