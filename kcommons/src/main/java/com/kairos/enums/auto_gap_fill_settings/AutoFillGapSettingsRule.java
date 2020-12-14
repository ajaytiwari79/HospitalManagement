package com.kairos.enums.auto_gap_fill_settings;

import com.kairos.dto.user.access_permission.AccessGroupRole;

import java.util.*;

public enum AutoFillGapSettingsRule {
    RULE1("Rule 1"), RULE2("Rule 2");
    private String autoFillGapSettingsRule;

    AutoFillGapSettingsRule() {}

    AutoFillGapSettingsRule(String autoFillGapSettingsRule) {
        this.autoFillGapSettingsRule = autoFillGapSettingsRule;
    }

    public static Set<AccessGroupRole> getAllAutoFillGapSettingsRules() {
        return new HashSet<>(EnumSet.allOf(AccessGroupRole.class));
    }

}
