package com.kairos.dto.gdpr.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClauseBasicDTO {


    private BigInteger id;

    @NotBlank(message = "error.message.title.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String title;

    @NotBlank(message = "error.message.description.notNull.orEmpty")
    private String description;

    private Boolean requireUpdate=false;

    @NotNull(message = "Clause order is Not defined")
    private Integer orderedIndex;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getRequireUpdate() {
        return requireUpdate;
    }

    public void setRequireUpdate(Boolean requireUpdate) {
        this.requireUpdate = requireUpdate;
    }

    public Integer getOrderedIndex() { return orderedIndex; }

    public void setOrderedIndex(Integer orderedIndex) { this.orderedIndex = orderedIndex; }

    public ClauseBasicDTO() {
    }
}
