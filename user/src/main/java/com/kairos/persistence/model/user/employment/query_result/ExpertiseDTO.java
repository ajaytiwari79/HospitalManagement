package com.kairos.persistence.model.user.employment.query_result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.activity.shift.ProtectedDaysOffSetting;
import com.kairos.dto.user.country.experties.SeniorityLevelDTO;
import com.kairos.dto.user.organization.union.SectorDTO;
import com.kairos.dto.user.organization.union.UnionIDNameDTO;
import com.kairos.enums.shift.BreakPaymentSetting;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by vipul on 30/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@QueryResult
public class ExpertiseDTO {
    private Long id;
    @NotBlank(message = "error.Expertise.name.notEmpty")
    private String name;
    private String description;
    private Long organizationLevelId;
    private List<Long> organizationServiceIds;
    private UnionIDNameDTO union;
    private int fullTimeWeeklyMinutes; // This is equals to 37 hours
    private int numberOfWorkingDaysInWeek; // 5 or 7
    private boolean published;
    private BreakPaymentSetting breakPaymentSetting;
    private SectorDTO sector;
    @Valid
    private List<SeniorityLevelDTO> seniorityLevels=new ArrayList<>();
    private LocalDate startDate;
    private LocalDate endDate;
    private Long expertiseLineId;
    private List<ProtectedDaysOffSetting> protectedDaysOffSettings;


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
