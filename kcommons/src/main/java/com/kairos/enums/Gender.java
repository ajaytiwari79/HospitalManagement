package com.kairos.enums;

import com.kairos.dto.user.country.filter.FilterDetailDTO;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by prabjot on 19/10/16.
 */
public enum Gender {

    MALE("Male"), FEMALE("Female");
    public String value;

    Gender(String value) {
        this.value = value;
    }

    public static Gender getByValue(String value) {
        for (Gender gender : Gender.values()) {
            if (gender.value.equals(value)) {
                return gender;
            }
        }
        return null;
    }

    public static List<FilterDetailDTO> getListOfGenderForFilters(){
        List<FilterDetailDTO> genderFilterData = new ArrayList<>();
        for(Gender gender : EnumSet.allOf(Gender.class)){
            FilterDetailDTO filterDetailDTO = new FilterDetailDTO(gender.name(), gender.value);
            genderFilterData.add(filterDetailDTO);
        }
        return genderFilterData;
    }

}
