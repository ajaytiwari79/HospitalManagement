package com.kairos.dto.user.skill;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
public class SkillDTO {
    private Set<Long> selectedSkillIds ;
    private Set<Long> removeSkillIds;
    private Boolean selected ;
}
