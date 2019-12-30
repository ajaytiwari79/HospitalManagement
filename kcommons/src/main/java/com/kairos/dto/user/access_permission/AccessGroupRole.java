package com.kairos.dto.user.access_permission;

import com.kairos.dto.user.country.filter.FilterDetailDTO;
import com.kairos.enums.Gender;

import java.util.*;

/**
 * Created by prerna on 21/3/18.
 */

public enum AccessGroupRole {
    STAFF("Staff"), MANAGEMENT("Management");
    private String accessGroupRole;

    AccessGroupRole() {

    }

    AccessGroupRole(String accessGroupRole) {
        this.accessGroupRole = accessGroupRole;
    }

    public static Set<AccessGroupRole> getAllRoles() {
        return new HashSet<>(EnumSet.allOf(AccessGroupRole.class));
    }

    public static List<FilterDetailDTO> getListOfAccessGroupRoleForFilters(){
        List<FilterDetailDTO> accessGroupRoleFilterData = new ArrayList<>();
        for(AccessGroupRole accessGroupRole : EnumSet.allOf(AccessGroupRole.class)){
            FilterDetailDTO filterDetailDTO = new FilterDetailDTO(accessGroupRole.name(), accessGroupRole.accessGroupRole);
            accessGroupRoleFilterData.add(filterDetailDTO);
        }
        return accessGroupRoleFilterData;
    }

}