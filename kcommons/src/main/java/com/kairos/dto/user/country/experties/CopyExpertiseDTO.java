package com.kairos.dto.user.country.experties;

import com.kairos.dto.user.organization.union.SectorDTO;
import com.kairos.dto.user.organization.union.UnionIDNameDTO;
import com.kairos.enums.shift.BreakPaymentSetting;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class CopyExpertiseDTO {
    private Long id;
    @NotBlank(message = "Expertise name is required")
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long organizationLevelId;
    private List<Long> organizationServiceIds;
    /*private Long unionId;*/
    private UnionIDNameDTO union;
    private Integer fullTimeWeeklyMinutes; // This is equals to 37 hours
    private Integer numberOfWorkingDaysInWeek; // 5 or 7

    private List<SeniorityLevelDTO> seniorityLevels;
    private List<Long> tags;
    private BreakPaymentSetting breakPaymentSetting;
    private Long parentId;
    // TODO REMOVE FOR FE compactibility
    private SectorDTO sector;

    @AssertTrue(message = "message.start_date.less_than.end_date")
    public boolean isValid() {
        if (!Optional.ofNullable(this.startDate).isPresent() && Optional.ofNullable(this.endDate).isPresent()) {
            return false;
        } else if (Optional.ofNullable(this.startDate).isPresent() && (Optional.ofNullable(this.endDate).isPresent())) {
            return startDate.isAfter(endDate);
        }
        return true;
    }
}
