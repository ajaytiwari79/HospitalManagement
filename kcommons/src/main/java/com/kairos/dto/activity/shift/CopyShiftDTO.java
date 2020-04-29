package com.kairos.dto.activity.shift;

import com.kairos.enums.Day;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Getter
@Setter
public class CopyShiftDTO {
    private List<BigInteger> shiftIds;
    private List<Long> staffIds;
    private Long expertiseId;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Day> selectedDays;
    private Boolean copyAttachedJobs;
    private Boolean copyAttachedNotes;
    private Boolean copyChatConversation;
    private Boolean includeStopBrick;
    private Boolean includeVetoDays;
    private Boolean includeAvailability;

    @AssertTrue(message = "'start date' must be less than 'end date'.")
    public boolean isValid() {
        if (!Optional.ofNullable(this.startDate).isPresent() || !Optional.ofNullable(this.endDate).isPresent()) {
            return false;
        }
        if (startDate.isEqual(endDate)) {
            return true;
        }
        return startDate.isBefore(endDate);

    }
}
