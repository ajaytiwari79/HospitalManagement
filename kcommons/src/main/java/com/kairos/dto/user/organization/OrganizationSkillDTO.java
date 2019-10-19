package com.kairos.dto.user.organization;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prerna on 9/11/17.
 */
@Getter
@Setter
public class OrganizationSkillDTO {

    private String customName;
    private List<Long> tags = new ArrayList<>();
}
