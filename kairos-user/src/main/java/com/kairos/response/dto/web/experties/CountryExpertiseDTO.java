package com.kairos.response.dto.web.experties;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by prerna on 14/11/17.
 */
public class CountryExpertiseDTO {

    private long id;

    @NotEmpty(message = "error.Expertise.name.notEmpty") @NotNull(message = "error.Expertise.name.notnull")
    private String name;

    @NotEmpty(message = "error.Expertise.description.notEmpty") @NotNull(message = "error.Expertise.description.notnull")
    private String description;

    private List<Long> tagsId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public List<Long> getTagsId() {
        return tagsId;
    }

    public void setTagsId(List<Long> tagsId) {
        this.tagsId = tagsId;
    }
}
