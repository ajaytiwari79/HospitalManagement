package com.kairos.dto.user.access_permission;
/*
 *Created By Pavan on 31/8/18
 *
 */

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StaffAccessGroupDTO {
    private Long staffId;
    private Long countryId;
    private Boolean isCountryAdmin;
    private List<Long> accessGroupIds;
}
