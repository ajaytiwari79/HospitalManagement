package com.kairos.enums;

import com.kairos.dto.user.country.filter.FilterDetailDTO;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Created By G.P.Ranjan on 18/10/19
 **/
public enum SkillLevel {

    BASIC("Basic"), ADVANCE("Advance"), EXPERT("Expert");
    public String value;

    SkillLevel(String value) {
        this.value = value;
    }

    public static SkillLevel getByValue(String value) {
        for (SkillLevel skillLevel : SkillLevel.values()) {
            if (skillLevel.value.equals(value)) {
                return skillLevel;
            }
        }
        return null;
    }

    public static List<FilterDetailDTO> getListOfSkillLevelForFilters(){
        List<FilterDetailDTO> genderFilterData = new ArrayList<>();
        for(SkillLevel skillLevel : EnumSet.allOf(SkillLevel.class)){
            FilterDetailDTO filterDetailDTO = new FilterDetailDTO(skillLevel.name(), skillLevel.value);
            genderFilterData.add(filterDetailDTO);
        }
        return genderFilterData;
    }
}
