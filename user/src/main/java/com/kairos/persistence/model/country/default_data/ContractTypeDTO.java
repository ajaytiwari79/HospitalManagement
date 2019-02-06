package com.kairos.persistence.model.country.default_data;

import org.hibernate.validator.constraints.Range;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@QueryResult
public class ContractTypeDTO {

    private Long id;
    @NotBlank(message = "error.ContractType.name.notEmpty")
    private String name;
    @NotNull(message = "error.ContractType.code.notEmpty")
    @Range(min = 1, message = "error.ContractType.code.greaterThenOne")
    private int code;
    private String description;

    public ContractTypeDTO() {
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
}
