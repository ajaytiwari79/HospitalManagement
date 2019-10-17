package com.kairos.dto.activity.wta.rule_template_category;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.kairos.dto.activity.tags.TagDTO;
import com.kairos.enums.RuleTemplateCategoryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by vipul on 2/8/17.
 * used to store Rule Template Category
 */


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class RuleTemplateCategoryDTO {


    @NotNull(message = "error.RuleTemplateCategory.name.notnull")
    // @JsonProperty(value = "categoryName")
    private String name;
    private String description;
    private RuleTemplateCategoryType ruleTemplateCategoryType;
    private Long country;
    private BigInteger id;
    private Date createdAt;
    private Date updatedAt;
    private boolean deleted;
    private List<BigInteger> ruleTemplateIds;

    private List<TagDTO> tags = new ArrayList<>();

    public String getName() {
        return name;
    }

    @JsonSetter("categoryName")
    public void setCategoryategoryName(String name) {
        if (this.name == null) {
            this.name = name;
        }
    }

    @JsonSetter("name")
    public void setName(String name) {
        this.name = name;
    }

}

