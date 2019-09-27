package com.kairos.dto.user.country.experties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.user.organization.union.SectorDTO;
import com.kairos.dto.user.organization.union.UnionIDNameDTO;
import com.kairos.enums.shift.BreakPaymentSetting;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by vipul on 30/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class ExpertiseDTO {
    private Long id;
    @NotBlank(message = "error.Expertise.name.notEmpty")
    private String name;
    private String description;
    private Date startDateMillis;
    private Date endDateMillis;
    private Long organizationLevelId;
    private Set<Long> organizationServiceIds;
    private UnionIDNameDTO union;
    private Integer fullTimeWeeklyMinutes; // This is equals to 37 hours
    private Integer numberOfWorkingDaysInWeek; // 5 or 7
    @Valid
    private SeniorityLevelDTO seniorityLevel;
    private boolean published;
    private BreakPaymentSetting breakPaymentSetting;
    private SectorDTO sector;
    @Valid
    private List<SeniorityLevelDTO> seniorityLevels;
    private LocalDate startDate;
    private LocalDate endDate;


    public void setEndDateMillis(Date endDateMillis) {
        this.endDateMillis = endDateMillis == null ? null : DateUtils.getEndOfDay(endDateMillis);
    }

    @AssertTrue(message = "message.start_date.less_than.end_date")
    public boolean isValid() {
        if (!Optional.ofNullable(this.startDateMillis).isPresent() && Optional.ofNullable(this.endDateMillis).isPresent()) {
            return false;
        } else if (Optional.ofNullable(this.startDateMillis).isPresent() && (Optional.ofNullable(this.endDateMillis).isPresent())) {
            DateTime endDateAsUtc = new DateTime(this.endDateMillis).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
            DateTime startDateAsUtc = new DateTime(this.startDateMillis).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
            return !endDateAsUtc.isBefore(startDateAsUtc);
        }
        return true;
    }
}
