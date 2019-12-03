package com.kairos.dto.user.access_permission;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by prabjot on 30/12/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class AccessGroupPermissionDTO {

    private List<Long> accessPageIds;
    private boolean isSelected;
}
