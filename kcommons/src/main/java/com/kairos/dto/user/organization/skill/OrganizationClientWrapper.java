package com.kairos.dto.user.organization.skill;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
@Getter
@Setter
@NoArgsConstructor
public class OrganizationClientWrapper {
    private List<Map<String, Object>> clientList;
    private Map<String, Object> timeSlotData;
    private Long staffId;

    public OrganizationClientWrapper(List<Map<String, Object>> clientList, Map<String, Object> timeSlotData) {
        this.clientList = clientList;
        this.timeSlotData = timeSlotData;
    }


}
