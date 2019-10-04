package com.kairos.dto.user.country.experties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.user.organization.union.SectorDTO;
import com.kairos.dto.user.organization.union.UnionIDNameDTO;
import com.kairos.enums.shift.BreakPaymentSetting;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by vipul on 29/3/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ExpertiseResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Long organizationLevelId;
    private Set<Long> organizationServiceIds;
    private UnionIDNameDTO union;
    private int fullTimeWeeklyMinutes;
    private Integer numberOfWorkingDaysInWeek;

    private List<SeniorityLevelDTO> seniorityLevels = new ArrayList<>();
    private Boolean published;
    private Boolean editable;
    private BreakPaymentSetting breakPaymentSetting;
    private SectorDTO sector;
    private LocalDate startDate;
    private LocalDate endDate;
}
