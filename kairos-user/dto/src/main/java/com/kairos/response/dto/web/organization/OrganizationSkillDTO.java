package com.kairos.response.dto.web.organization;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by prerna on 9/11/17.
 */
public class OrganizationSkillDTO {

    private String customName;

    private String visitourId;

    private List<Long> tags = new ArrayList<Long>();

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public String getVisitourId() {
        return visitourId;
    }

    public void setVisitourId(String visitourId) {
        this.visitourId = visitourId;
    }

    public List<Long> getTags() {
        return tags;
    }

    public void setTags(List<Long> tags) {
        this.tags = tags;
    }
}
