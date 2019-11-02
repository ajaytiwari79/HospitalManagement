package com.kairos.dto.user.organization;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by prerna on 26/2/18.
 */
@Getter
@Setter
@NoArgsConstructor
public class OrganizationCategoryDTO {

    private String name;

    private String value;

    private int count;
    public OrganizationCategoryDTO(String name, String value){
        this.name = name;
        this.value = value;
    }

}
