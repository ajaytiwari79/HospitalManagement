package com.kairos.dto.user.country.skill;

import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.user.skill.SkillLevelDTO;
import com.kairos.dto.user_context.UserContext;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by prerna on 14/11/17.
 */
@Getter
@Setter
@NoArgsConstructor
public class SkillDTO {

    private Long id;
    @NotBlank(message = "error.SkillCategory.name.notEmpty")
    private String name;
    private String description;
    private String shortName;
    private List<Long> tags;
    private Set<SkillLevelDTO> skillLevels;
    private Long visitourId;
    private String customName;
    private boolean isEdited;
    private Long countryId;
    private Map<String,String> translatedNames;
    private Map<String,String> translatedDescriptions;
    private Map<String, TranslationInfo> translations;


    public SkillDTO(Long id, @NotBlank(message = "error.SkillCategory.name.notEmpty") String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getName() {
        if(TranslationUtil.isVerifyTranslationDataOrNotForName(translations)) {
            return translations.get(UserContext.getUserDetails().getLanguage()).getName();
        }else {
            return name;
        }
    }

    public String getDescription() {
        if(TranslationUtil.isVerifyTranslationDataOrNotForDescription(translations)) {
            return translations.get(UserContext.getUserDetails().getLanguage()).getDescription();
        }else {
            return description;
        }
    }



//    @AssertTrue(message = "Please provide valid dates")
//    public boolean isValid() {
//        SkillLevelDTO advanceSkill=skillLevels.stream().filter(k->k.getSkillLevel().equals(SkillLevel.ADVANCE)).findAny().orElse(null);
//        SkillLevelDTO basicSkill=skillLevels.stream().filter(k->k.getSkillLevel().equals(SkillLevel.BASIC)).findAny().orElse(null);
//        SkillLevelDTO expertSkill=skillLevels.stream().filter(k->k.getSkillLevel().equals(SkillLevel.EXPERT)).findAny().orElse(null);
//        if(advanceSkill!=null && basicSkill !=null && advanceSkill.getStartDate().isAfter(basicSkill.getEndDate()) && advanceSkill.getEndDate().isBefore(expertSkill.getStartDate())){
//            return true;
//        }
//        return false;
//    }

}
