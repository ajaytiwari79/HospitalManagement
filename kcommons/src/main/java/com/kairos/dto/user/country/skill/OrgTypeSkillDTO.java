package com.kairos.dto.user.country.skill;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by prabjot on 12/4/17.
 */
@Getter
@Setter
public class OrgTypeSkillDTO {

    private Long skillId;
    @JsonProperty("isSelected")
    private boolean isSelected;
}
