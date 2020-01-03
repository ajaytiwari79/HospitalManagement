package com.kairos.dto.user.country.experties;

import com.kairos.dto.user.organization.union.SectorDTO;
import com.kairos.dto.user.organization.union.UnionIDNameDTO;
import com.kairos.enums.shift.BreakPaymentSetting;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class  ExpertiseLineDTO implements Comparable<ExpertiseLineDTO> {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long organizationLevelId;
    private Set<Long> organizationServiceIds;
    private UnionIDNameDTO union;
    private int fullTimeWeeklyMinutes;
    private Integer numberOfWorkingDaysInWeek;
    private List<SeniorityLevelDTO> seniorityLevels = new ArrayList<>();
    private Boolean editable;
    private BreakPaymentSetting breakPaymentSetting;
    private SectorDTO sector;

    @Override
    public int compareTo(ExpertiseLineDTO expertiseLineDTO) {
        if (getStartDate() == null || expertiseLineDTO.getStartDate() == null) {
            return 0;
        }
        return getStartDate().compareTo(expertiseLineDTO.getStartDate());
    }

}
