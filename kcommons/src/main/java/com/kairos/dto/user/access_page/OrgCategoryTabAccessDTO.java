package com.kairos.dto.user.access_page;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.OrganizationCategory;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Created by prerna on 28/2/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class OrgCategoryTabAccessDTO {

    @NotNull(message = "error.org.category.notnull")
    private OrganizationCategory organizationCategory;
    @NotNull(message = "error.org.access.status.notnull")
    private Boolean accessStatus;
}
