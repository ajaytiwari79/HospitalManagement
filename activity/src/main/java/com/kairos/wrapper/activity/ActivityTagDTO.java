package com.kairos.wrapper.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.annotation.PermissionClass;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.activity.activity.activity_tabs.CompositeShiftActivityDTO;
import com.kairos.dto.user.country.tag.TagDTO;
import com.kairos.enums.ActivityStateEnum;
import com.kairos.enums.OrganizationHierarchy;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.tabs.BalanceSettingsActivityTab;
import com.kairos.persistence.model.activity.tabs.GeneralActivityTab;
import com.kairos.persistence.model.activity.tabs.TimeCalculationActivityTab;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.RulesActivityTab;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by prerna on 16/12/17.
 */

@Getter
@Setter
@PermissionClass(name = "Activity")
public class ActivityTagDTO {
    private BigInteger id;
    private String name;
    private String description;
    private Long countryId;
    private BigInteger categoryId;
    private String categoryName;
    private List<TagDTO> tags = new ArrayList<>();
    private Long unitId;
    private boolean isParentActivity = true;
    private GeneralActivityTab generalActivityTab;
    private BalanceSettingsActivityTab balanceSettingsActivityTab;
    private LocalDate startDate;
    private LocalDate endDate;
    private TimeCalculationActivityTab timeCalculationActivityTab;
    private List<Long> dayTypes= new ArrayList<>();
    private RulesActivityTab rulesActivityTab;
    private Boolean activityCanBeCopied=false;
    private Set<OrganizationHierarchy> activityCanBeCopiedForOrganizationHierarchy;
    private Long parentId;
    private ActivityStateEnum state;
    private List<CompositeShiftActivityDTO> compositeActivities;
    private BigInteger activityPriorityId;
    private boolean allowChildActivities;
    private boolean applicableForChildActivities;
    private boolean sicknessSettingValid;
    private Set<BigInteger> childActivityIds=new HashSet<>();
    // for filter FullDay and Full week activity
    private String methodForCalculatingTime;
    private Map<String, TranslationInfo> translations=new HashMap<>() ;

    public ActivityTagDTO() {
        //default constructor
    }


    public ActivityTagDTO buildActivityTagDTO(Activity activity, List<TagDTO> tags) {
        this.id = activity.getId();
        this.name = activity.getName();
        this.description = activity.getDescription();
        this.isParentActivity = activity.isParentActivity();
        this.unitId = activity.getUnitId();
        this.tags = tags;
        this.state = activity.getState();

        return this;
    }
}
