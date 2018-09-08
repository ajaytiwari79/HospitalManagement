package com.kairos.enums;


import com.kairos.dto.user.organization.OrganizationCategoryDTO;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by prerna on 26/2/18.
 */
public enum OrganizationCategory {
    HUB("Hub"), UNION("Union"), ORGANIZATION("Organization");
    public String value;
    OrganizationCategory(String value) {
        this.value = value;
    }

    public static List<OrganizationCategoryDTO> getListOfOrganizationCategory(){
        List<OrganizationCategoryDTO> organizationCategoryList = new ArrayList<>();
        for(OrganizationCategory organizationCategory : EnumSet.allOf(OrganizationCategory.class)){
            OrganizationCategoryDTO currentValue = new OrganizationCategoryDTO(organizationCategory.value, organizationCategory.name());
            organizationCategoryList.add(currentValue);
        }
        return organizationCategoryList;
    }
}
