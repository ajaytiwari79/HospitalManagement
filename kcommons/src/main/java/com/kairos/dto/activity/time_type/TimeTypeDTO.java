package com.kairos.dto.activity.time_type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.OrganizationHierarchy;
import com.kairos.enums.TimeTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class TimeTypeDTO {
    private BigInteger id;
    private String timeTypes;
    private String label;
    private String description;
    private BigInteger upperLevelTimeTypeId;
    private boolean selected;
    private List<TimeTypeDTO> children = new ArrayList<>();
    private String backgroundColor;
    private TimeTypeEnum secondLevelType;
    private Set<OrganizationHierarchy> activityCanBeCopiedForOrganizationHierarchy;
    private boolean partOfTeam;
    private boolean allowChildActivities;
    private List<TimeTypeDTO> parent = new ArrayList<>();
    private boolean allowedConflicts;
    private boolean breakNotHeldValid;

    public TimeTypeDTO() {
    }



    public TimeTypeDTO(String timeTypes, String backgroundColor) {
        this.timeTypes = timeTypes;
        this.backgroundColor = backgroundColor;
    }


    public TimeTypeDTO(BigInteger id, String timeTypes, BigInteger upperLevelTimeTypeId) {
        this.id = id;
        this.timeTypes = timeTypes;
        this.upperLevelTimeTypeId = upperLevelTimeTypeId;
    }

    public TimeTypeDTO(BigInteger id, String timeTypes, String label, String description,String backgroundColor,Set<OrganizationHierarchy> activityCanBeCopiedForOrganizationHierarchy, boolean partOfTeam, boolean allowChildActivities,boolean allowedConflicts,boolean breakNotHeldValid) {
        this.id = id;
        this.timeTypes = timeTypes;
        this.label = label;
        this.description = description;
        this.backgroundColor=backgroundColor;
        this.activityCanBeCopiedForOrganizationHierarchy = activityCanBeCopiedForOrganizationHierarchy;
        this.partOfTeam = partOfTeam;
        this.allowChildActivities = allowChildActivities;
        this.allowedConflicts=allowedConflicts;
        this.breakNotHeldValid = breakNotHeldValid;
    }


}
