package com.kairos.dto.user.country.agreement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by prerna on 10/1/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateRuleTemplateCategoryDTO {
    @NotBlank(message = "error.RuleTemplate.description.notEmpty")
    private String name;
    private String description;
    private List<Long> tags;
    private BigInteger id;

    UpdateRuleTemplateCategoryDTO(){}

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
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

    public List<Long> getTags() {
        return tags;
    }

    public void setTags(List<Long> tags) {
        this.tags = tags;
    }
}
