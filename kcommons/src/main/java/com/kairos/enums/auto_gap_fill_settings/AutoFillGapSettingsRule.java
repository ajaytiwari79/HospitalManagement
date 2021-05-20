package com.kairos.enums.auto_gap_fill_settings;


import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Getter
@NoArgsConstructor
public enum AutoFillGapSettingsRule implements Serializable {
    HIGHEST_RANKED_ACTIVITY_PLANNED_ADJACENT_TO_THE_GAP("Fill the gap with the highest ranked activity planned adjacent to the gap"),
    HIGHEST_RANKED_ACTIVITY_IF_HIGHEST_IS_CAUSING_GAP_THEN_USE_SECOND_HIGHEST("Always fill with the highest ranked activity, if highest is causing gap then use the second highest (planned or not in the shift)."),
    HIGHEST_RANKED_ACTIVITY_IF_IT_IS_SOLVING_MORE_PROBLEMS_THAN_CAUSING("Fill the gap with the highest ranked activity if it is solving more problems than causing (planned or not in the shift)."),
    HIGHEST_RANKED_ACTIVITY_PLANNED_ADJACENT_TO_THE_GAP_SOLVING_MORE_PROBLEMS_THAN_CAUSING("Fill the gap with the highest ranked activity planned adjacent to the gap solving more problems than causing"),
    HIGHEST_RANKED_ACTIVITY_THAT_IS_SOLVING_MORE_PROBLEMS_THAN_CAUSING("Fill the gap with the highest ranked activity that is solving more problems than causing"),
    DO_NOT_ALLOW_TO_CAUSE_GAP("Do not allow to cause gap");

    private String autoFillGapSettingsRule;

    AutoFillGapSettingsRule(String autoFillGapSettingsRule) {
        this.autoFillGapSettingsRule = autoFillGapSettingsRule;
    }

    public static List<NameValuePair> getAllAutoFillGapSettingsRules() {
        List<NameValuePair> returnValue = new ArrayList<>();
        EnumSet.allOf(AutoFillGapSettingsRule.class).forEach(autoFillGapSettingsRule -> {
            returnValue.add(new BasicNameValuePair(autoFillGapSettingsRule.name(), autoFillGapSettingsRule.autoFillGapSettingsRule));
        });
        return returnValue;
    }

}
