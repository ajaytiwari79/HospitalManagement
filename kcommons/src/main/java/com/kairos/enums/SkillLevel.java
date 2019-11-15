package com.kairos.enums;

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
}
