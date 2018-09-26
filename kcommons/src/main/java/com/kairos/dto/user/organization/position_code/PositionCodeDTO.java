package com.kairos.dto.user.organization.position_code;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Created by prerna on 26/2/18.
 */
public class PositionCodeDTO {
    private Long id;
    @NotEmpty(message = "error.PositionCode.name.notempty")
    @NotNull(message = "error.position_code.name.notnull")
    private String name;

    private String description;

    private String timeCareId;

    public PositionCodeDTO() {
        // default constructor
    }

    public PositionCodeDTO(String name, String description, String timeCareId) {
        this.name = name;
        this.description = description;
        this.timeCareId = timeCareId;
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

    public String getTimeCareId() {
        return timeCareId;
    }

    public void setTimeCareId(String timeCareId) {
        this.timeCareId = timeCareId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
