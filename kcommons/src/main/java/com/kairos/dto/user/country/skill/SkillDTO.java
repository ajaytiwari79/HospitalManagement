package com.kairos.dto.user.country.skill;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;

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


    public SkillDTO(Long id, @NotBlank(message = "error.SkillCategory.name.notEmpty") String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

}
