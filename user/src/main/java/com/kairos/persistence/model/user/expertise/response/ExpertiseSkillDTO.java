package com.kairos.persistence.model.user.expertise.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by prabjot on 4/4/17.
 */
@Getter
@Setter
public class ExpertiseSkillDTO {

    private List<Long> skillIds;
    private boolean isSelected;

}
