package com.kairos.persistence.model.user.expertise.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.shift.BreakPaymentSetting;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.services.OrganizationService;
import com.kairos.persistence.model.organization.union.Location;
import com.kairos.persistence.model.organization.union.Sector;
import com.kairos.persistence.model.pay_table.PayTable;
import com.kairos.persistence.model.user.expertise.CareDays;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by vipul on 28/3/18.
 */
@QueryResult
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class ExpertiseQueryResult {
    private String name;
    private String description;
    private Long startDateMillis;
    private Long endDateMillis;
    private Integer fullTimeWeeklyMinutes;
    private Integer numberOfWorkingDaysInWeek;
    private Long id;
    private Boolean published;
    private Boolean history;
    private List<CareDays> seniorDays;
    private List<CareDays> childCareDays;
    private Level organizationLevel;
    private List<OrganizationService> organizationService;
    //TODO in current unwinded property cant be set to any nested domain to QueryResult DTO , We will change if in feature this will handle
    private Organization union;
    private PayTable payTable;
    private List<Map<String, Object>> seniorityLevels;
    private BreakPaymentSetting breakPaymentSetting;
    private Sector sector;
    private Map<String,Object> unionRepresentative;// in case of expertise at unit level only
    private Location unionLocation;// in case of expertise at unit level only
    private Set<Long> supportedUnitIds;
    private List<ExpertiseLineQueryResult> expertiseLineQueryResults;
}
