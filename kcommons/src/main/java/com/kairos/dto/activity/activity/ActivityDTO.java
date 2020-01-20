package com.kairos.dto.activity.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.planning_setting.PlanningSetting;
import com.kairos.constants.CommonConstants;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
public class ActivityDTO  {
    private BigInteger id;
    @NotBlank(message = "message.activity.name.notEmpty")
    private String name;
    private List<Long> expertises;
    private String description;
    private Long countryId;
    private BigInteger categoryId;
    private String categoryName;
    private Long unitId = -1L;
    private List<Long> employmentTypes;
    private boolean isParentActivity = true;
    private GeneralActivityTabDTO generalActivityTab;
    private TimeTypeDTO timeType;
    private TimeCalculationActivityDTO timeCalculationActivityTab;
    private RulesActivityTabDTO rulesActivityTab;
    private List<ActivityDTO> childActivities;
    private BalanceSettingActivityTabDTO balanceSettingsActivityTab;
    private Long countryActivityId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigInteger parentId;
    private PhaseSettingsActivityTab phaseSettingsActivityTab;
    private List<Long> skills;
    private SkillActivityDTO skillActivityTab;
    private Boolean activityCanBeCopied=false;
    private ActivityPriorityDTO activityPriority;
    private List<ShiftStatus> activityStatus;


    //    private List<Tag> tags;
//    private List<BigInteger> tags = new ArrayList<>();
    private List<BigInteger> tags = new ArrayList<>();
    private boolean allowChildActivities;
    private Set<BigInteger> childActivityIds;
    private BigInteger activityPriorityId;
    private int activitySequence;
    private BigInteger countryParentId;



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
        this.balanceSettingsActivityTab = new BalanceSettingActivityTabDTO(timeTypeId);
    }
    public void setActivityCanBeCopied(Boolean activityCanBeCopied) {
        this.activityCanBeCopied = activityCanBeCopied==null?false:activityCanBeCopied;
    }

    public boolean isFullDayOrFullWeekActivity() {
        return isNotNull(this.getTimeCalculationActivityTab()) && ((CommonConstants.FULL_WEEK).equals(this.getTimeCalculationActivityTab().getMethodForCalculatingTime()) || (CommonConstants.FULL_DAY_CALCULATION).equals(this.getTimeCalculationActivityTab().getMethodForCalculatingTime())); }

}
