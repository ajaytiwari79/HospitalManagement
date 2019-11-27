package com.kairos.dto.user.country.skill;

import com.kairos.enums.SkillLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import java.util.List;
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


    public SkillDTO(Long id, @NotBlank(message = "error.SkillCategory.name.notEmpty") String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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
