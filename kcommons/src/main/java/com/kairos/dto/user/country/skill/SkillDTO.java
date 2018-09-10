package com.kairos.dto.user.country.skill;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by prerna on 14/11/17.
 */
public class SkillDTO {

    private Long id;

    @NotEmpty(message = "error.SkillCategory.name.notEmpty") @NotNull(message = "error.SkillCategory.name.notnull")
    private String name;

    //@NotEmpty(message = "error.SkillCategory.description.notEmpty") @NotNull(message = "error.SkillCategory.description.notnull")
    private String description;

    private String shortName;

    private List<Long> tags;

    public SkillDTO() {
    }

    public SkillDTO(Long id, @NotEmpty(message = "error.SkillCategory.name.notEmpty") @NotNull(message = "error.SkillCategory.name.notnull") String name, String description) {
        this.id = id;
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

    public List<Long> getTags() {
        return tags;
    }

    public void setTags(List<Long> tags) {
        this.tags = tags;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
