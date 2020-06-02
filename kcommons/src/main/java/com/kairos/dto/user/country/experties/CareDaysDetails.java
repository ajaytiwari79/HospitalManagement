package com.kairos.dto.user.country.experties;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class CareDaysDetails {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean published;
    private Long expertiseId;
    private List<AgeRangeDTO> careDays;

    @AssertTrue(message = "message.start_date.less_than.end_date")
    public boolean isValid() {
        if (!Optional.ofNullable(this.startDate).isPresent() && Optional.ofNullable(this.endDate).isPresent()) {
            return false;
        } else if (Optional.ofNullable(this.startDate).isPresent() && (Optional.ofNullable(this.endDate).isPresent())) {
            return !startDate.isAfter(endDate);
        }
        return true;
    }

}
