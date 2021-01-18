package com.kairos.wrapper.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.annotation.PermissionClass;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Created by vipul on 6/12/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@PermissionClass(name = "Activity")
public class ActivityWithSelectedDTO implements Serializable {
    private List<ActivityTagDTO> allActivities;
    private List<ActivityTagDTO> selectedActivities;
}
