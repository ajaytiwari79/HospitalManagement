package com.kairos.persistence.model.user.expertise.response;

import com.kairos.enums.shift.BreakPaymentSetting;
import com.kairos.persistence.model.organization.services.OrganizationService;
import com.kairos.persistence.model.pay_table.PayTable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@QueryResult
@Getter
@Setter
public class ExpertiseLineQueryResult {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<OrganizationService> organizationServices;
    private List<Map<String, Object>> seniorityLevels;
    private PayTable payTable;
    private Long expertiseId;
    private int fullTimeWeeklyMinutes;
    private int numberOfWorkingDaysInWeek;
    private BreakPaymentSetting breakPaymentSetting;

}
