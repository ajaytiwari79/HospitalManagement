package com.kairos.dto.activity.counter.data;

import com.kairos.enums.DurationType;
import com.kairos.enums.kpi.KPIRepresentation;
import com.kairos.enums.wta.IntervalUnit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Positive;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import static com.kairos.constants.MessageConstants.FREQUENCY_VALUE_IS_NOT_POSITIVE;

@Getter
@Setter
@NoArgsConstructor
public class FilterCriteriaDTO {
    private Long countryId;
    private boolean isCountryAdmin;
    private boolean management;
    private Long unitId;
    private Long staffId;
    private List<FilterCriteria> filters;
    private List<BigInteger> kpiIds;
    private List<BigInteger> counterIds;
    private DurationType frequencyType;
    // frequency value
    @Positive(message = FREQUENCY_VALUE_IS_NOT_POSITIVE)
    private int value;
    private KPIRepresentation kpiRepresentation;
    private IntervalUnit interval;
    private LocalDate startDate;
    private LocalDate endDate;

    public FilterCriteriaDTO(List<FilterCriteria> filters, List<BigInteger> kpiIds,Long countryId,boolean isCountryAdmin,KPIRepresentation kpiRepresentation,IntervalUnit interval,int value,DurationType frequencyType) {
        this.filters = filters;
        this.kpiIds = kpiIds;
        this.countryId=countryId;
        this.isCountryAdmin=isCountryAdmin;
        this.value=value;
        this.frequencyType=frequencyType;
        this.kpiRepresentation=kpiRepresentation;
        this.interval=interval;
    }

    public FilterCriteriaDTO(Long unitId,Long staffId,List<BigInteger> kpiIds,Long countryId,boolean isCountryAdmin) {
        this.countryId=countryId;
        this.staffId=staffId;
        this.isCountryAdmin=isCountryAdmin;
        this.unitId = unitId;
        this.kpiIds = kpiIds;
    }

    public FilterCriteriaDTO(boolean isCountryAdmin, Long staffId, List<BigInteger> kpiIds, KPIRepresentation kpiRepresentation, List<FilterCriteria> filters, IntervalUnit interval, DurationType frequencyType, int value,Long unitId) {
        this.isCountryAdmin = isCountryAdmin;
        this.staffId = staffId;
        this.kpiIds = kpiIds;
        this.kpiRepresentation = kpiRepresentation;
        this.filters = filters;
        this.interval = interval;
        this.frequencyType = frequencyType;
        this.value = value;
        this.unitId = unitId;
    }
}
