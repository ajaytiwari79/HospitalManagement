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
    RULES_AS_PER_STAFF_PRODUCTIVE_TYPE_ON_BOTH_SIDE_REQUEST_PHASE1("Check if either side of the activity falls in main team. If it is, fill the gap with that activity."),
    /* Changed */RULES_AS_PER_STAFF_PRODUCTIVE_TYPE_ON_BOTH_SIDE_REQUEST_PHASE2("Fill with main team assigned to the staff in subject. But if main team causes gap, fill with highest secondary team assigned to the staff (even if it is not planned in the shift, causing problems, or breaking continuity)"),
    RULES_AS_PER_STAFF_PRODUCTIVE_TYPE_ON_BOTH_SIDE_REQUEST_PHASE3("Fill the gap with the activity of secondary teams with the highest rank planned in the same shift."),
    RULES_AS_PER_STAFF_PRODUCTIVE_TYPE_ON_BOTH_SIDE_PUZZLE_TO_TENTATIVE_PHASE1("Fill the gap with the activity of the main team not planned in the shift, if having partial or complete understaffing."),
    RULES_AS_PER_STAFF_PRODUCTIVE_TYPE_ON_BOTH_SIDE_PUZZLE_TO_TENTATIVE_PHASE2("Fill the gap with the highest ranked activity of the team solving complete/partial understaffing. Including main and secondary"),
    RULES_AS_PER_STAFF_PRODUCTIVE_TYPE_ON_BOTH_SIDE_PUZZLE_TO_TENTATIVE_PHASE3("Check for the parent activities assigned to the staff other than the planned activities that solves maximum duration. If more than one solves maximum duration then pick the one with highest rank"),
    RULES_AS_PER_STAFF_PRODUCTIVE_TYPE_ON_BOTH_SIDE_PUZZLE_TO_TENTATIVE_PHASE4("Do not allow to delete the activity when all of the assigned activities filling the gap would only create overstaffing."),
    RULES_AS_PER_STAFF_ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE_REQUEST_PHASE1("The productive type of activity would cover up the gap."),
    RULES_AS_PER_STAFF_ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE_PUZZLE_TO_TENTATIVE_PHASE1("The productive type of activity would cover up the gap if it has partial or complete understaffing."),
    RULES_AS_PER_STAFF_ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE_PUZZLE_TO_TENTATIVE_PHASE2("Check for the productive activities assigned to the staff other than the planned activities that solves maximum duration. If more than one solves maximum duration then pick the one with highest rank"),
    RULES_AS_PER_STAFF_ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE_PUZZLE_TO_TENTATIVE_PHASE3("Fill the gap with the same indirect activity as that in the shift."),
    //RULES_AS_PER_STAFF_ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE_REAL_TIME_PHASE1("Fill the gap with the staff’s main team’s activity."),
    RULES_AS_PER_STAFF_NON_PRODUCTIVE_TYPE_ON_BOTH_SIDE_ALL_PHASE1("The system would pick up anyone of the planned activity randomly as indirect activity will not have ranking amongst themselves."),
    RULES_AS_PER_MANAGEMENT_PRODUCTIVE_TYPE_ON_BOTH_SIDE_REQUEST_PHASE1("Check between the two teams that which one is of higher rank as per the unit ranking, that team will fill the gap. "),
    RULES_AS_PER_MANAGEMENT_PRODUCTIVE_TYPE_ON_BOTH_SIDE_REQUEST_PHASE2("Fill the gap with the highest ranked productive activity assigned to the staff in subject (even if it is causing overstaffing or breaking continuity)"),
    RULES_AS_PER_MANAGEMENT_PRODUCTIVE_TYPE_ON_BOTH_SIDE_PUZZLE_TO_TENTATIVE_PHASE1("Check if the highest ranked parent activity not planned in the shift solves partial or complete understaffing"),
    RULES_AS_PER_MANAGEMENT_PRODUCTIVE_TYPE_ON_BOTH_SIDE_PUZZLE_TO_TENTATIVE_PHASE2("Fill the gap with the parent activities planned in the shift when atleast one activity is solving complete/partial understaffing."),
    RULES_AS_PER_MANAGEMENT_PRODUCTIVE_TYPE_ON_BOTH_SIDE_PUZZLE_TO_TENTATIVE_PHASE3("Check for the parent activities assigned to the staff other than the planned activities that solves maximum duration. If more than one solves maximum duration then pick the one with highest rank as per unit."),
    RULES_AS_PER_MANAGEMENT_PRODUCTIVE_TYPE_ON_BOTH_SIDE_REAL_TIME_PHASE1("In this case, fill the gap with the activity assigned to the staff of highest rank as per unit."),
    RULES_AS_PER_MANAGEMENT_ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE_REAL_TIME_PHASE1("In this case, fill the gap with the activity of highest rank as per unit.");
    //RULES_AS_PER_MANAGEMENT_PRODUCTIVE_TYPE_ON_BOTH_SIDE_PUZZLE_TO_TENTATIVE_PHASE4("Do not allow to delete the activity when all of the assigned activities filling the gap would only create overstaffing."),
    //RULES_AS_PER_MANAGEMENT_PRODUCTIVE_TYPE_ON_BOTH_SIDE_PUZZLE_TO_TENTATIVE_PHASE5("Fill the gap with the highest ranked productive activity assigned to the staff in subject (even if it is causing overstaffing or breaking continuity)"),
    //RULES_AS_PER_MANAGEMENT_ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE_REQUEST_PHASE1("The productive type of activity would cover up the gap."),
    //RULES_AS_PER_MANAGEMENT_ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE_PUZZLE_TO_TENTATIVE_PHASE1("The productive type of activity would cover up the gap if it has partial or complete understaffing."),
    //RULES_AS_PER_MANAGEMENT_ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE_PUZZLE_TO_TENTATIVE_PHASE2("Check for the productive activities assigned to the staff other than the planned activities that solves maximum duration. If more than one solves maximum duration then pick the one with highest rank"),
    //RULES_AS_PER_MANAGEMENT_ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE_PUZZLE_TO_TENTATIVE_PHASE3("Fill the gap with the same indirect activity as that in the shift."),
    //RULES_AS_PER_MANAGEMENT_NON_PRODUCTIVE_TYPE_ON_BOTH_SIDE_ALL_PHASE1("The system would pick up anyone of the planned activity randomly as indirect activity will not have ranking amongst themselves.");

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
