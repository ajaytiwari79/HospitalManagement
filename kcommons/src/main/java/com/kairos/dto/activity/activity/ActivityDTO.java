package com.kairos.dto.activity.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.annotation.PermissionClass;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.constants.CommonConstants;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.activity.activity.activity_tabs.*;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.enums.shift.ShiftStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;


/**
 * Created by prerna on 6/12/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@Builder
@AllArgsConstructor
@PermissionClass(name = "Activity")
public class ActivityDTO  {
    private BigInteger id;
    @NotBlank(message = "message.activity.name.notEmpty")
    private String name;
    private List<Long> expertises;
    private String description;
    private Long countryId;
    private BigInteger categoryId;
    private String categoryName;
    @Builder.Default
    private Long unitId = -1L;
    private List<Long> employmentTypes;
    @Builder.Default
    private boolean isParentActivity = true;
    private ActivityGeneralSettingsDTO activityGeneralSettings;
    private TimeTypeDTO timeType;
    private TimeCalculationActivityDTO activityTimeCalculationSettings;
    private ActivityRulesSettingsDTO activityRulesSettings;
    private List<ActivityDTO> childActivities;
    private ActivityBalanceSettingDTO activityBalanceSettings;
    private Long countryActivityId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigInteger parentId;
    private ActivityPhaseSettings activityPhaseSettings;
    private List<Long> skills;
    private SkillActivityDTO activitySkillSettings;
    @Builder.Default
    private Boolean activityCanBeCopied=false;
    private ActivityPriorityDTO activityPriority;
    private List<ShiftStatus> activityStatus;
    @Builder.Default
    private List<BigInteger> tags = new ArrayList<>();
    private boolean allowChildActivities;
    private Set<BigInteger> childActivityIds;
    private BigInteger activityPriorityId;
    private int activitySequence;
    private BigInteger countryParentId;
    private Long teamId;
    @Builder.Default
    private Map<String, TranslationInfo> translations = new HashMap<>();


    public ActivityDTO() {
        //default constructor
    }

    public ActivityDTO(BigInteger id, String name, BigInteger parentId) {
        this.id = id;
        this.name = StringUtils.trim(name);
        this.parentId = parentId;
    }

    public ActivityDTO(String name, String description, Long countryId, String categoryName, Long unitId, boolean isParentActivity) {
        this.name = StringUtils.trim(name);
        this.description = StringUtils.trim(description);
        this.countryId = countryId;
        this.categoryName = categoryName;
        this.unitId = unitId;
        this.isParentActivity = isParentActivity;
    }

    public ActivityDTO(BigInteger timeTypeId){
        this.activityBalanceSettings = new ActivityBalanceSettingDTO(timeTypeId);
    }
    public void setActivityCanBeCopied(Boolean activityCanBeCopied) {
        this.activityCanBeCopied = activityCanBeCopied==null?false:activityCanBeCopied;
    }

    public boolean isFullDayOrFullWeekActivity() {
        return isNotNull(this.getActivityTimeCalculationSettings()) && ((CommonConstants.FULL_WEEK).equals(this.getActivityTimeCalculationSettings().getMethodForCalculatingTime()) || (CommonConstants.FULL_DAY_CALCULATION).equals(this.getActivityTimeCalculationSettings().getMethodForCalculatingTime()));
    }

    public String getName(){
        return TranslationUtil.getName(translations,name);
    }

    public String getDescription(){
        return TranslationUtil.getDescription(translations,description);
    }


}
