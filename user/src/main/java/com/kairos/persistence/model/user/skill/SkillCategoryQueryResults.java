package com.kairos.persistence.model.user.skill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.user.country.skill.SkillDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.persistence.model.common.UserTranslationInfoConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Map;

@QueryResult
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class SkillCategoryQueryResults {
    private Long id;
    private String name;
    private String description;
    private List<Skill> skillList;
    private Long countryId;
    private Map<String,String> translatedNames;
    private Map<String,String> translatedDescriptions;
    @Convert(UserTranslationInfoConverter.class)
    private Map<String, TranslationInfo> translations;
    private List<SkillDTO> children;


    public SkillCategoryQueryResults(Long id, String name, String description, List<SkillDTO> children, Map<String, TranslationInfo> translations) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.children = children;
        this.translations = translations;
    }


    public String getName() {
        return TranslationUtil.getName(TranslationUtil.convertUnmodifiableMapToModifiableMap(translations),name);
    }

    public String getDescription() {
        return  TranslationUtil.getDescription(TranslationUtil.convertUnmodifiableMapToModifiableMap(translations),description);
    }
}
