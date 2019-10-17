package com.kairos.dto.activity.time_bank;

import com.kairos.dto.activity.time_bank.time_bank_basic.time_bank.CTADistributionDTO;
import com.kairos.dto.activity.time_bank.time_bank_basic.time_bank.ScheduledActivitiesDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author pradeep
 * @date - 17/8/18
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeBankVisualViewDTO {

    private long  timeBankMinutes;
    private long presenceScheduledMinutes;
    private long absenceScheduledMinutes;
    private long timeBankChange;

    private List<TimeBankIntervalDTO> timeBankIntervals;
    private List<ScheduledActivitiesDTO> scheduledActivities;
    private List<CTADistributionDTO> timeBankCTADistributions;



}
