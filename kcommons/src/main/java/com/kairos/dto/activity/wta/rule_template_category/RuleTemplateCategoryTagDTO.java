package com.kairos.dto.activity.wta.rule_template_category;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.activity.tags.TagDTO;
import com.kairos.dto.user_context.UserContext;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private Map<String, TranslationInfo> translations;
    private Long countryId;

    public String getName() {
        if(TranslationUtil.isVerifyTranslationDataOrNotForName(translations)) {
            return translations.get(UserContext.getUserDetails().getLanguage()).getName();
        }else {
            return name;
        }
    }
    
}
