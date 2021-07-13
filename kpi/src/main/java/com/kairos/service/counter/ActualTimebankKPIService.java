package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.counter.enums.XAxisConfig;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.dto.user.country.experties.ExpertiseLineDTO;
import com.kairos.dto.user.employment.EmploymentLinesDTO;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.service.time_bank.TimeBankCalculationService;
import com.kairos.utils.counter.KPIUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;

import static com.kairos.commons.utils.DateUtils.getHourByMinutes;
import static com.kairos.commons.utils.DateUtils.getHoursByMinutes;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.dto.activity.counter.enums.XAxisConfig.AVERAGE_PER_DAY;
import static com.kairos.dto.activity.counter.enums.XAxisConfig.HOURS;
import static com.kairos.enums.FilterType.EMPLOYMENT_SUB_TYPE;
import static com.kairos.utils.counter.KPIUtils.getValueWithDecimalFormat;
import static java.util.stream.Collectors.toMap;

@Service
public class ActualTimebankKPIService implements KPIService {

    @Inject private TimeBankCalculationService timeBankCalculationService;

    @Override
    public <T> double get(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, T t) {
        return getActualTimeBank(staffId,kpiCalculationRelatedInfo);
    }

    public double getActualTimeBank(Long staffId, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = ObjectUtils.isNotNull(staffId) ? Arrays.asList(kpiCalculationRelatedInfo.getStaffIdAndStaffKpiFilterMap().getOrDefault(staffId, new StaffKpiFilterDTO())) : kpiCalculationRelatedInfo.getStaffKpiFilterDTOS();
        Long actualTimeBank = 0l;
        Long actualTimeBankPerDay = 0l;
        if (ObjectUtils.isCollectionNotEmpty(staffKpiFilterDTOS)) {
            Long[] actualTimebankDetails = calculateActualTimeBank(staffKpiFilterDTOS,kpiCalculationRelatedInfo);
            actualTimeBank = actualTimebankDetails[0];
            actualTimeBankPerDay = actualTimebankDetails[1];
        }

        if (XAxisConfig.HOURS.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0))) {
            return DateUtils.getHoursByMinutes(actualTimeBank);
        }
        return actualTimeBankPerDay;
    }

    private Long[] calculateActualTimeBank(List<StaffKpiFilterDTO> staffKpiFilterDTOS, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        Long actualTimeBank = 0l;
        Long actualTimeBankPerDay = 0l;
        for (StaffKpiFilterDTO staffKpiFilterDTO : staffKpiFilterDTOS) {
            for (EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO : staffKpiFilterDTO.getEmployment()) {
                EmploymentLinesDTO employmentLinesDTO = getSortedEmploymentLine(employmentWithCtaDetailsDTO);
                if (ObjectUtils.isNotNull(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.EMPLOYMENT_SUB_TYPE)) && ObjectUtils.isNotNull(employmentLinesDTO.getEmploymentSubType())) {
                    if (kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.EMPLOYMENT_SUB_TYPE).get(0).equals(employmentLinesDTO.getEmploymentSubType().name())) {
                        ActualTimeBank actualTimeBank1 = new ActualTimeBank(kpiCalculationRelatedInfo, actualTimeBank, actualTimeBankPerDay, employmentWithCtaDetailsDTO).invoke();
                        actualTimeBank = actualTimeBank1.getActualTimeBankDetails();
                        actualTimeBankPerDay = actualTimeBank1.getActualTimeBankPerDay();

                    }
                } if(ObjectUtils.isNull(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.EMPLOYMENT_SUB_TYPE))){
                    ActualTimeBank actualTimeBank1 = new ActualTimeBank(kpiCalculationRelatedInfo, actualTimeBank, actualTimeBankPerDay, employmentWithCtaDetailsDTO).invoke();
                    actualTimeBank = actualTimeBank1.getActualTimeBankDetails();
                    actualTimeBankPerDay = actualTimeBank1.getActualTimeBankPerDay();
                }
            }
        }
        return new Long[]{actualTimeBank,actualTimeBankPerDay};
    }

    public EmploymentLinesDTO getSortedEmploymentLine(EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO) {
        List<EmploymentLinesDTO> employmentLinesDTOS = employmentWithCtaDetailsDTO.getEmploymentLines();
        Collections.sort(employmentLinesDTOS);
        //Collections.reverse(employmentLinesDTOS);
        return employmentLinesDTOS.get(employmentLinesDTOS.size()-1);
    }

    private class ActualTimeBank {
        private KPICalculationRelatedInfo kpiCalculationRelatedInfo;
        private Long actualTimeBankDetails;
        private Long actualTimeBankPerDay;
        private EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO;

        public ActualTimeBank(KPICalculationRelatedInfo kpiCalculationRelatedInfo, Long actualTimeBankDetails, Long actualTimeBankPerDay, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO) {
            this.kpiCalculationRelatedInfo = kpiCalculationRelatedInfo;
            this.actualTimeBankDetails = actualTimeBankDetails;
            this.actualTimeBankPerDay = actualTimeBankPerDay;
            this.employmentWithCtaDetailsDTO = employmentWithCtaDetailsDTO;
        }

        public Long getActualTimeBankDetails() {
            return actualTimeBankDetails;
        }

        public Long getActualTimeBankPerDay() {
            return actualTimeBankPerDay;
        }

        public ActualTimeBank invoke() {
            Double numberOfDaysInWeekOfAStaff = getNumberOfWorkingDays(employmentWithCtaDetailsDTO);
            if(numberOfDaysInWeekOfAStaff==0){
                return this;
            }else {
                List<DailyTimeBankEntry> dailyTimeBankEntries = (List) kpiCalculationRelatedInfo.getEmploymentIdAndDailyTimebankEntryMap().getOrDefault(employmentWithCtaDetailsDTO.getId(), new ArrayList<>());
                Map<LocalDate, DailyTimeBankEntry> dateDailyTimeBankEntryMap = dailyTimeBankEntries.stream().collect(toMap(DailyTimeBankEntry::getDate, v -> v));
                if (XAxisConfig.AVERAGE_PER_DAY.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0))) {
                    actualTimeBankDetails = timeBankCalculationService.calculateActualTimebank(kpiCalculationRelatedInfo.getPlanningPeriodInterval(), dateDailyTimeBankEntryMap, employmentWithCtaDetailsDTO, kpiCalculationRelatedInfo.getPlanningPeriodInterval().getEndLocalDate(), employmentWithCtaDetailsDTO.getStartDate(),null);
                    actualTimeBankPerDay += Math.round(DateUtils.getHourByMinutes(actualTimeBankDetails) / numberOfDaysInWeekOfAStaff);
                } else {
                    actualTimeBankDetails += timeBankCalculationService.calculateActualTimebank(kpiCalculationRelatedInfo.getPlanningPeriodInterval(), dateDailyTimeBankEntryMap, employmentWithCtaDetailsDTO, kpiCalculationRelatedInfo.getPlanningPeriodInterval().getEndLocalDate(), employmentWithCtaDetailsDTO.getStartDate(),null);
                }
            }
            return this;
        }

        public Double getNumberOfWorkingDays(EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO) {
            List<ExpertiseLineDTO> expertiseLineDTOS = employmentWithCtaDetailsDTO.getExpertise().getExpertiseLines();
            EmploymentLinesDTO employmentLinesDTO =getSortedEmploymentLine(employmentWithCtaDetailsDTO);
            Collections.sort(expertiseLineDTOS);
            return KPIUtils.getValueWithDecimalFormat(Double.valueOf(employmentLinesDTO.getTotalWeeklyHours())/Double.valueOf(expertiseLineDTOS.get(expertiseLineDTOS.size()-1).getNumberOfWorkingDaysInWeek()));
        }
    }
}
