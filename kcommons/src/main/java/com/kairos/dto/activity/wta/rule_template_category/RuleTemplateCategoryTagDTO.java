package com.kairos.dto.activity.wta.rule_template_category;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.activity.tags.TagDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by prerna on 29/11/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class RuleTemplateCategoryTagDTO {

    private BigInteger id;
    private String name;
    private String description;
    private List<TagDTO> tags = new ArrayList<>();
    private List<BigInteger> ruleTemplateIds;
}
