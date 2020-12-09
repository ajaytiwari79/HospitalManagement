package com.kairos.enums.gap_settings;

import com.kairos.dto.user.access_permission.AccessGroupRole;

import java.util.*;

public enum GapSettingsRule {
    RULE1("Rule 1"), RULE2("Rule 2");
    private String gapSettingsRule;

    GapSettingsRule() {}

    GapSettingsRule(String gapSettingsRule) {
        this.gapSettingsRule = gapSettingsRule;
    }

    public static Set<AccessGroupRole> getAllGapSettingsRules() {
        return new HashSet<>(EnumSet.allOf(AccessGroupRole.class));
    }

}
