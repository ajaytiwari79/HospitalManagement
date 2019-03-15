package com.kairos.service.payroll_setting;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.payroll_setting.PayrollAccessGroupsDTO;
import com.kairos.dto.activity.payroll_setting.PayrollPeriodDTO;
import com.kairos.dto.activity.payroll_setting.UnitPayrollSettingDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.payroll_setting.PayrollFrequency;
import com.kairos.persistence.model.payroll_setting.PayrollAccessGroups;
import com.kairos.persistence.model.payroll_setting.PayrollPeriod;
import com.kairos.persistence.model.payroll_setting.UnitPayrollSetting;
import com.kairos.persistence.repository.payroll_setting.UnitPayrollSettingMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;

@Service
public class UnitPayrollSettingService extends MongoBaseService {

    @Inject
    private UnitPayrollSettingMongoRepository unitPayrollSettingMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    public UnitPayrollSettingDTO getDefaultDataOfPayrollPeriod(Long unitId, PayrollFrequency payrollFrequency) {
        List<UnitPayrollSetting> unitPayrollSettings = unitPayrollSettingMongoRepository.findAllByUnitIdAndFrequency(unitId, payrollFrequency);
        return new UnitPayrollSettingDTO(isCollectionNotEmpty(unitPayrollSettings) ? unitPayrollSettings.stream().flatMap(unitPayrollSetting -> unitPayrollSetting.getPayrollPeriods().stream().map(payrollPeriod -> payrollPeriod.getStartDate().getYear())).collect(Collectors.toSet()) : new HashSet<>());
    }

    public List<UnitPayrollSettingDTO> getPayrollPeriodByUnitIdAndDateAndDurationType(Long unitId, Integer year, PayrollFrequency payrollFrequency) {
        return getUnitPayrollSettingData(unitPayrollSettingMongoRepository.getPayrollPeriodByYearAndPayrollFrequency(unitId, payrollFrequency, DateUtils.getFirstDayOfYear(year), getlastDayOfYear(year)));

    }

    public UnitPayrollSettingDTO createPayrollPeriod(UnitPayrollSettingDTO unitPayrollSettingDTO, Long unitId) {
        List<UnitPayrollSetting> unitPayrollSettings = new ArrayList<>();
        validatePayrollPeriod(unitPayrollSettingDTO, unitId);
        unitPayrollSettingMongoRepository.findAndDeletePayrollPeriodByUnitIdAndPayrollFrequency(unitId, unitPayrollSettingDTO.getPayrollFrequency());
        List<LocalDate> payrollStartDates = new ArrayList<>();
        payrollStartDates.add(unitPayrollSettingDTO.getStartDate());
        if (unitPayrollSettingDTO.getStartDate().plusYears(1).minusDays(1).isAfter(getlastDayOfYear(unitPayrollSettingDTO.getStartDate()))) {
            payrollStartDates.add(getFirstDayOfNextYear(unitPayrollSettingDTO.getStartDate()));
        }
        LocalDate endDate = null;
        for (LocalDate payrollStartDate : payrollStartDates) {
            List<PayrollPeriod> payrollPeriods = new ArrayList<>();
            if (PayrollFrequency.FORTNIGHTLY.equals(unitPayrollSettingDTO.getPayrollFrequency()) && isNotNull(endDate)) {
                payrollStartDate = endDate;
            }
            LocalDate payrollEndDate = payrollStartDate.isBefore(getlastDayOfYear(unitPayrollSettingDTO.getStartDate())) ? getlastDayOfYear(unitPayrollSettingDTO.getStartDate()) : unitPayrollSettingDTO.getStartDate().plusYears(1);
            while (payrollStartDate.isBefore(payrollEndDate)) {
                LocalDate payrollPeriodEndDate = getNextStartDate(payrollStartDate, unitPayrollSettingDTO.getPayrollFrequency());
                payrollPeriods.add(new PayrollPeriod(payrollStartDate, payrollPeriodEndDate.minusDays(1)));
                payrollStartDate = payrollPeriodEndDate;
            }
            unitPayrollSettings.add(new UnitPayrollSetting(false, unitId, payrollPeriods, unitPayrollSettingDTO.getPayrollFrequency()));
            endDate = payrollPeriods.get(payrollPeriods.size() - 1).getEndDate().plusDays(1);
        }
        unitPayrollSettingMongoRepository.saveEntities(unitPayrollSettings);
        return ObjectMapperUtils.copyPropertiesByMapper(unitPayrollSettings.get(0), UnitPayrollSettingDTO.class);
    }


    public UnitPayrollSettingDTO deleteDraftPayrollPeriod(BigInteger payrollPeriodId, Long unitId) {
        UnitPayrollSetting unitPayrollSetting = unitPayrollSettingMongoRepository.findPayrollPeriodByUnitIdPayrollPeriodId(unitId, payrollPeriodId);
        if (isNull(unitPayrollSetting)) {
            exceptionService.actionNotPermittedException("message.payroll.period.not.found");
        }
        // use when draft table deleted and return previous data of parent table
        UnitPayrollSetting parentUnitPayrollSetting = unitPayrollSettingMongoRepository.findPayrollPeriodByUnitIdPayrollPeriodId(unitId, unitPayrollSetting.getParentPayrollId());
        unitPayrollSettingMongoRepository.removeDraftPayrollPeriodByUnitAndPayrollPeriodId(unitId, unitPayrollSetting.getId());
        return ObjectMapperUtils.copyPropertiesByMapper(parentUnitPayrollSetting, UnitPayrollSettingDTO.class);
    }

    public UnitPayrollSettingDTO updatePayrollPeriod(UnitPayrollSettingDTO unitPayrollSettingDTO, Long unitId) {
        List<UnitPayrollSetting> unitPayrollSettings = new ArrayList<>();
        UnitPayrollSetting unitPayrollSetting = unitPayrollSettingMongoRepository.findPayrollPeriodByUnitIdPayrollPeriodId(unitId, unitPayrollSettingDTO.getId());
        if (isNull(unitPayrollSetting)) {
            exceptionService.actionNotPermittedException("message.payroll.period.not.found");
        }
        unitPayrollSetting.setAccessGroupsPriority(ObjectMapperUtils.copyPropertiesOfListByMapper(unitPayrollSettingDTO.getAccessGroupsPriority(), PayrollAccessGroups.class));
        unitPayrollSetting.setPublished(unitPayrollSettingDTO.isPublished());
        if (!unitPayrollSettingDTO.isPublished()) {
            unitPayrollSetting.setPayrollPeriods(ObjectMapperUtils.copyPropertiesOfListByMapper(unitPayrollSettingDTO.getPayrollPeriods(), PayrollPeriod.class));
            unitPayrollSetting.setAccessGroupsPriority(ObjectMapperUtils.copyPropertiesOfListByMapper(unitPayrollSettingDTO.getAccessGroupsPriority(), PayrollAccessGroups.class));
        } else {
            unitPayrollSetting.setPayrollPeriods(validatePayrollPeriod(unitPayrollSettingDTO.getPayrollPeriods(), unitPayrollSetting.getPayrollPeriods()));
            UnitPayrollSetting availableDraftPayroll = unitPayrollSettingMongoRepository.findDraftPayrollPeriodByUnitIdAndPayrollParentIdNotExist(unitPayrollSetting.getId(), unitId, unitPayrollSetting.getPayrollFrequency());
            if (isNotNull(unitPayrollSetting.getParentPayrollId())) {
                unitPayrollSettings.add(updateParentPayrollPeriod(unitId, unitPayrollSetting.getParentPayrollId(), unitPayrollSetting.getPayrollPeriods()));
            }
            if (isNotNull(availableDraftPayroll)) {
                unitPayrollSettings.add(publishDraftPayrollPeriod(unitPayrollSetting, availableDraftPayroll));
            }
        }
        unitPayrollSettings.add(unitPayrollSetting);
        unitPayrollSettingMongoRepository.saveEntities(unitPayrollSettings);
        return ObjectMapperUtils.copyPropertiesByMapper(unitPayrollSetting, UnitPayrollSettingDTO.class);
    }

    // when first payroll period create and table is break between two year then if one payroll table is published than second table also publish
    private UnitPayrollSetting publishDraftPayrollPeriod(UnitPayrollSetting unitPayrollSetting, UnitPayrollSetting availableDraftPayroll) {
        int daysTillDeadlineDate = getDurationBetweenTwoLocalDates(unitPayrollSetting.getPayrollPeriods().get(unitPayrollSetting.getPayrollPeriods().size() - 1).getEndDate(), unitPayrollSetting.getPayrollPeriods().get(unitPayrollSetting.getPayrollPeriods().size() - 1).getDeadlineDate().toLocalDate(), DurationType.DAYS).intValue();
        for (PayrollPeriod payrollPeriod : availableDraftPayroll.getPayrollPeriods()) {
            payrollPeriod.setPayrollAccessGroups(unitPayrollSetting.getPayrollPeriods().get(unitPayrollSetting.getPayrollPeriods().size() - 1).getPayrollAccessGroups());
            payrollPeriod.setDeadlineDate(getLocalDateTimeFromLocalDateAndLocalTime(payrollPeriod.getEndDate().plusDays(daysTillDeadlineDate), unitPayrollSetting.getPayrollPeriods().get(unitPayrollSetting.getPayrollPeriods().size() - 1).getDeadlineDate().toLocalTime()));
        }
        availableDraftPayroll.setAccessGroupsPriority(unitPayrollSetting.getAccessGroupsPriority());
        availableDraftPayroll.setPublished(true);
        return availableDraftPayroll;
    }

    public List<UnitPayrollSettingDTO> breakPayrollPeriodOfUnit(Long unitId, UnitPayrollSettingDTO unitPayrollSettingDTO) {
        UnitPayrollSetting unitPayrollSetting = unitPayrollSettingMongoRepository.findPayrollPeriodByIdAndPayrollFrequency(unitId, unitPayrollSettingDTO.getParentPayrollId(), unitPayrollSettingDTO.getPayrollFrequency());
        if (isNull(unitPayrollSetting)) {
            exceptionService.actionNotPermittedException("message.payroll.period.not.found");
        } else if (unitPayrollSettingDTO.getStartDate().isBefore(DateUtils.getLocalDate()) || !validateStartDateForPayrollPeriodCreation(unitPayrollSettingDTO.getStartDate(), unitPayrollSettingDTO.getPayrollFrequency())) {
            exceptionService.actionNotPermittedException("error.payroll.period.start.date.invalid");
        }
        UnitPayrollSetting draftUnitPayrollSetting = unitPayrollSettingMongoRepository.findDraftPayrollPeriodByUnitIdAndPayrollFrequencyAndEndDate(unitId, unitPayrollSetting.getPayrollFrequency(), getlastDayOfYear(unitPayrollSettingDTO.getEndDate().getYear()));
        Map<LocalDate, PayrollPeriod> startDateAndPayrollPeriodMap = unitPayrollSetting.getPayrollPeriods().stream().collect(Collectors.toMap(PayrollPeriod::getStartDate, Function.identity()));
        UnitPayrollSetting newUnitPayrollSetting = null;
        if (isNotNull(draftUnitPayrollSetting)) {
            newUnitPayrollSetting = draftUnitPayrollSetting;
            newUnitPayrollSetting = updateOldDraftStateOfPayrollPeriod(unitPayrollSettingDTO, startDateAndPayrollPeriodMap, newUnitPayrollSetting);
        } else {
            newUnitPayrollSetting = getNewDraftStateOfPayroll(unitPayrollSettingDTO, unitPayrollSetting, startDateAndPayrollPeriodMap, newUnitPayrollSetting, unitId);
        }
        unitPayrollSettingMongoRepository.save(newUnitPayrollSetting);
        List<UnitPayrollSettingDTO> unitPayrollSettingDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(Arrays.asList(newUnitPayrollSetting, unitPayrollSetting), unitPayrollSettingDTO.getClass());
        return getUnitPayrollSettingData(unitPayrollSettingDTOS);
    }

    private UnitPayrollSetting updateOldDraftStateOfPayrollPeriod(UnitPayrollSettingDTO unitPayrollSettingDTO, Map<LocalDate, PayrollPeriod> startDateAndPayrollPeriodMap, UnitPayrollSetting newUnitPayrollSetting) {
        Map<LocalDate, PayrollPeriod> startDateAndDraftPayrollPeriodMap = newUnitPayrollSetting.getPayrollPeriods().stream().collect(Collectors.toMap(PayrollPeriod::getStartDate, Function.identity()));
        LocalDate payrollStartDate = unitPayrollSettingDTO.getStartDate();
        while (payrollStartDate.isBefore(unitPayrollSettingDTO.getEndDate())) {
            if (startDateAndPayrollPeriodMap.containsKey(payrollStartDate) && !startDateAndDraftPayrollPeriodMap.containsKey(payrollStartDate)) {
                newUnitPayrollSetting.getPayrollPeriods().add(startDateAndPayrollPeriodMap.get(payrollStartDate));
            }
            payrollStartDate = getNextStartDate(payrollStartDate, unitPayrollSettingDTO.getPayrollFrequency());
        }
        return newUnitPayrollSetting;
    }

    private UnitPayrollSetting updateParentPayrollPeriod(Long unitId, BigInteger parentPayrollId, List<PayrollPeriod> payrollPeriod) {
        UnitPayrollSetting unitPayrollSetting = unitPayrollSettingMongoRepository.findPayrollPeriodByUnitIdPayrollPeriodId(unitId, parentPayrollId);
        if (isNotNull(unitPayrollSetting)) {
            Map<LocalDate, PayrollPeriod> startDateAndPayrollPeriodMap = unitPayrollSetting.getPayrollPeriods().stream().collect(Collectors.toMap(PayrollPeriod::getStartDate, Function.identity()));
            for (PayrollPeriod period : payrollPeriod) {
                if (startDateAndPayrollPeriodMap.containsKey(period.getStartDate())) {
                    startDateAndPayrollPeriodMap.remove(period.getStartDate());
                }
            }
            unitPayrollSetting.setPayrollPeriods(startDateAndPayrollPeriodMap.keySet().stream().map(date -> startDateAndPayrollPeriodMap.get(date)).collect(Collectors.toList()));
            //unitPayrollSettingMongoRepository.save(unitPayrollSetting);
        }
        return unitPayrollSetting;
    }


    private UnitPayrollSetting getNewDraftStateOfPayroll(UnitPayrollSettingDTO unitPayrollSettingDTO, UnitPayrollSetting unitPayrollSetting, Map<LocalDate, PayrollPeriod> startDateAndPayrollPeriodMap, UnitPayrollSetting newUnitPayrollSetting, Long unitId) {
        List<PayrollPeriod> payrollPeriods = new ArrayList<>();
        newUnitPayrollSetting = new UnitPayrollSetting();
        newUnitPayrollSetting.setPublished(unitPayrollSettingDTO.isPublished());
        newUnitPayrollSetting.setUnitId(unitId);
        newUnitPayrollSetting.setParentPayrollId(unitPayrollSettingDTO.getParentPayrollId());
        newUnitPayrollSetting.setPayrollFrequency(unitPayrollSettingDTO.getPayrollFrequency());
        LocalDate payrollStartDate = unitPayrollSettingDTO.getStartDate();
        while (payrollStartDate.isBefore(unitPayrollSettingDTO.getEndDate())) {
            if (startDateAndPayrollPeriodMap.containsKey(payrollStartDate)) {
                payrollPeriods.add(startDateAndPayrollPeriodMap.get(payrollStartDate));
            }
            payrollStartDate = getNextStartDate(payrollStartDate, unitPayrollSettingDTO.getPayrollFrequency());
        }
        newUnitPayrollSetting.setPayrollPeriods(payrollPeriods);
        newUnitPayrollSetting.setAccessGroupsPriority(unitPayrollSetting.getAccessGroupsPriority());
        return newUnitPayrollSetting;
    }

    private List<PayrollPeriod> validatePayrollPeriod(List<PayrollPeriodDTO> payrollPeriodDTOS, List<PayrollPeriod> payrollPeriods) {
        Map<LocalDate, PayrollPeriod> startDateAndPayrollPeriodMap = payrollPeriods.stream().collect(Collectors.toMap(PayrollPeriod::getStartDate, Function.identity()));
        for (PayrollPeriodDTO payrollPeriodDTO : payrollPeriodDTOS) {
            if (startDateAndPayrollPeriodMap.containsKey(payrollPeriodDTO.getStartDate())) {
                if (isNull(payrollPeriodDTO.getDeadlineDate()) || DateUtils.getLocalDateFromLocalDateTime(payrollPeriodDTO.getDeadlineDate()).isBefore(payrollPeriodDTO.getStartDate()) ) {
                    exceptionService.actionNotPermittedException("message.payroll.deadline.date.not.invalid", payrollPeriodDTO.getStartDate(), payrollPeriodDTO.getEndDate());
                }
                startDateAndPayrollPeriodMap.get(payrollPeriodDTO.getStartDate()).setDeadlineDate(payrollPeriodDTO.getDeadlineDate());
                int daysTillDeadlineDate = (int) ChronoUnit.DAYS.between(payrollPeriodDTO.getEndDate(), payrollPeriodDTO.getDeadlineDate());
                if (veryfyGracePeriodOfAccessGroup(payrollPeriodDTO.getPayrollAccessGroups(), daysTillDeadlineDate, payrollPeriodDTO.getStartDate(), payrollPeriodDTO.getEndDate())) {
                    startDateAndPayrollPeriodMap.get(payrollPeriodDTO.getStartDate()).setPayrollAccessGroups(ObjectMapperUtils.copyPropertiesOfListByMapper(payrollPeriodDTO.getPayrollAccessGroups(), PayrollAccessGroups.class));
                }
            }
        }
        return startDateAndPayrollPeriodMap.keySet().stream().map(date -> startDateAndPayrollPeriodMap.get(date)).collect(Collectors.toList());
    }

    private boolean veryfyGracePeriodOfAccessGroup(List<PayrollAccessGroupsDTO> payrollAccessGroupsDTOS, int daysTillDeadlineDate, LocalDate startDate, LocalDate endDate) {
        boolean result = isCollectionNotEmpty(payrollAccessGroupsDTOS);
        int sumOfGracePeriod = result ? payrollAccessGroupsDTOS.stream().mapToInt(payrollAccessGroupsDTOs -> payrollAccessGroupsDTOs.getGracePeriod()).sum() : 0;
        if (sumOfGracePeriod == 0 || sumOfGracePeriod > daysTillDeadlineDate || !result) {
            exceptionService.actionNotPermittedException("message.payroll.grace.period.not.invalid", daysTillDeadlineDate, startDate, endDate);
        }
        return true;
    }

    private LocalDate getNextStartDate(LocalDate oldDate, PayrollFrequency payrollFrequency) {
        return PayrollFrequency.MONTHLY.equals(payrollFrequency) ? oldDate.plusMonths(1) : oldDate.plusWeeks(2);
    }

    private boolean validateStartDateForPayrollPeriodCreation(LocalDate startDate, PayrollFrequency payrollFrequency) {
        return payrollFrequency.equals(PayrollFrequency.FORTNIGHTLY) ? startDate.getDayOfWeek().equals(DayOfWeek.MONDAY) : startDate.equals(startDate.with(TemporalAdjusters.firstDayOfMonth()));
    }

    private List<UnitPayrollSettingDTO> getUnitPayrollSettingData(List<UnitPayrollSettingDTO> unitPayrollSettingDTO) {
        Map<BigInteger, UnitPayrollSettingDTO> payrollIdAndPayrollSettingDTOMap = unitPayrollSettingDTO.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
        UnitPayrollSettingDTO unitPayrollSettingDto = unitPayrollSettingDTO.stream().filter(payrollSetting -> !payrollSetting.isPublished()).findFirst().orElse(null);
        if (isNotNull(unitPayrollSettingDto) && payrollIdAndPayrollSettingDTOMap.containsKey(unitPayrollSettingDto.getParentPayrollId())) {
            List<PayrollPeriodDTO> payrollPeriod = payrollIdAndPayrollSettingDTOMap.get(unitPayrollSettingDto.getParentPayrollId()).getPayrollPeriods();
            unitPayrollSettingDto.getPayrollPeriods().forEach(payrollPeriodDTO -> {
                payrollPeriod.removeIf(v -> v.getStartDate().isEqual(payrollPeriodDTO.getStartDate()));
            });
            payrollIdAndPayrollSettingDTOMap.get(unitPayrollSettingDto.getParentPayrollId()).setPayrollPeriods(payrollPeriod);
        }
        unitPayrollSettingDTO=new ArrayList<>(payrollIdAndPayrollSettingDTOMap.values());
        unitPayrollSettingDTO.sort((Comparator.comparing(UnitPayrollSettingDTO::getId)));
        return unitPayrollSettingDTO;
    }

    private boolean validatePayrollPeriod(UnitPayrollSettingDTO unitPayrollSettingDTO, Long unitId) {
        if (unitPayrollSettingDTO.getStartDate().isBefore(DateUtils.getLocalDate()) || !validateStartDateForPayrollPeriodCreation(unitPayrollSettingDTO.getStartDate(), unitPayrollSettingDTO.getPayrollFrequency())) {
            exceptionService.actionNotPermittedException("error.payroll.period.start.date.invalid");
        }
        boolean existsPayrollPeriod = unitPayrollSettingMongoRepository.findPayrollPeriodByUnitIdStartDate(unitId, unitPayrollSettingDTO.getStartDate());
        if (existsPayrollPeriod) {
            exceptionService.actionNotPermittedException("message.payroll.period.date.alreadyexists");
        }
        return true;
    }
}
