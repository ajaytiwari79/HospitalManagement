package com.kairos.dto.user.staff.staff_settings;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
@Getter
@Setter
public class StaffAndActivitySettingWrapper {
    private Set<Long> staffIds;
    private List<StaffActivitySettingDTO> staffActivitySettings;
}
