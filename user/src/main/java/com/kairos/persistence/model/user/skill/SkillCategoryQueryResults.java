package com.kairos.persistence.model.user.skill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.user.country.skill.SkillDTO;
import com.kairos.dto.user_context.UserContext;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Map;

@QueryResult
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SkillCategoryQueryResults {
    private Long id;
    private String name;
    private String description;
    private List<SkillDTO> skillList;
    private Long countryId;
    private Map<String,String> translatedNames;
    private Map<String,String> translatedDescriptions;
    private Map<String, TranslationInfo> translations;

    public String getName() {
        return TranslationUtil.getName(translations,name);
    }

    public String getDescription() {
        return  TranslationUtil.getDescription(translations,description);
    }
}
