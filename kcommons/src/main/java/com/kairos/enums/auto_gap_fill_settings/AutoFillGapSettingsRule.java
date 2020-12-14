package com.kairos.enums.auto_gap_fill_settings;


import java.util.*;

public enum AutoFillGapSettingsRule {
    RULE1("Rule 1"), RULE2("Rule 2");
    private String autoFillGapSettingsRule;

    AutoFillGapSettingsRule() {}

    AutoFillGapSettingsRule(String autoFillGapSettingsRule) {
        this.autoFillGapSettingsRule = autoFillGapSettingsRule;
    }

    public static Set<AutoFillGapSettingsRule> getAllAutoFillGapSettingsRules() {
        return new HashSet<>(EnumSet.allOf(AutoFillGapSettingsRule.class));
    }

}
