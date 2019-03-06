package com.kairos.service.payroll_setting;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.payroll_setting.PayrollAccessGroupsDTO;
import com.kairos.dto.activity.payroll_setting.PayrollPeriodDTO;
import com.kairos.dto.activity.payroll_setting.UnitPayrollSettingDTO;
import com.kairos.enums.payroll_setting.PayrollFrequency;
import com.kairos.persistence.model.payroll_setting.PayrollAccessGroups;
import com.kairos.persistence.model.payroll_setting.PayrollPeriod;
import com.kairos.persistence.model.payroll_setting.UnitPayrollSetting;
import com.kairos.persistence.repository.payroll_setting.UnitUnitPayrollSettingMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;

@Service
public class UnitPayrollSettingService extends MongoBaseService {

    @Inject
    private UnitUnitPayrollSettingMongoRepository unitPayrollSettingMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    public UnitPayrollSettingDTO getDefaultDataOfPayrollPeriod(Long unitId, PayrollFrequency payrollFrequency) {
        List<UnitPayrollSetting> unitPayrollSettings = unitPayrollSettingMongoRepository.findAllByunitIdAndFrequency(unitId, payrollFrequency);
        return new UnitPayrollSettingDTO(isCollectionNotEmpty(unitPayrollSettings) ? (Set) unitPayrollSettings.stream().flatMap(unitPayrollSetting -> unitPayrollSetting.getPayrollPeriods().stream().map(payrollPeriod -> payrollPeriod.getStartDate().getYear())).collect(Collectors.toSet()) : new HashSet<>());
    }

    public List<UnitPayrollSettingDTO> getPayrollPeriodByUnitIdAndDateAndDurationType(Long unitId, Integer year, PayrollFrequency payrollFrequency) {
        return getFilterPayrollSettingData(unitPayrollSettingMongoRepository.getPayrollPeriodByYearAndPayrollFrequency(unitId, payrollFrequency, DateUtils.getFirstDayOfYear(year), DateUtils.getlastDayOfYear(year)));

    }

    public UnitPayrollSettingDTO createPayrollPeriod(UnitPayrollSettingDTO unitPayrollSettingDTO, Long unitId) {
        List<UnitPayrollSetting> unitPayrollSettings = new ArrayList<>();
        if (!validateStartDateForpayrollPeriodCreation(unitPayrollSettingDTO.getStartDate(), unitPayrollSettingDTO.getPayrollFrequency())) {
            exceptionService.actionNotPermittedException("error.payroll.period.start.date.invalid");
        }
        unitPayrollSettingMongoRepository.findAndDeletePayrollPeriodByStartDate(unitId);
        List<LocalDate> payrollStartDates = new ArrayList<>();
        payrollStartDates.add(unitPayrollSettingDTO.getStartDate());
//        if (unitPayrollSettingDTO.getStartDate().plusYears(1).minusDays(1).isAfter(DateUtils.getlastDayOfYear(unitPayrollSettingDTO.getStartDate().getYear()))) {
//            payrollStartDates.add(DateUtils.getlastDayOfYear(unitPayrollSettingDTO.getStartDate().getYear()).plusDays(1));
//        }
        List<PayrollPeriod> payrollPeriods;
        for (LocalDate payrollStartDate : payrollStartDates) {
            payrollPeriods = new ArrayList<>();
            LocalDate payrollEndDate = (payrollStartDate.isBefore(DateUtils.getlastDayOfYear(unitPayrollSettingDTO.getStartDate().getYear())) ? DateUtils.getlastDayOfYear(unitPayrollSettingDTO.getStartDate().getYear()) : unitPayrollSettingDTO.getStartDate().plusYears(1));
            while (payrollStartDate.isBefore(payrollEndDate)) {
                payrollPeriods.add(new PayrollPeriod(payrollStartDate, getNextStartDate(payrollStartDate, unitPayrollSettingDTO.getPayrollFrequency()).minusDays(1)));
                payrollStartDate = getNextStartDate(payrollStartDate, unitPayrollSettingDTO.getPayrollFrequency());
            }
            unitPayrollSettings.add(new UnitPayrollSetting(false, unitId, payrollPeriods, unitPayrollSettingDTO.getPayrollFrequency()));
        }
        unitPayrollSettingMongoRepository.saveEntities(unitPayrollSettings);
        return ObjectMapperUtils.copyPropertiesByMapper(unitPayrollSettings.get(0), UnitPayrollSettingDTO.class);
    }

    public UnitPayrollSettingDTO deleteDraftPayrollPeriod(BigInteger id, Long unitId) {
        UnitPayrollSetting unitPayrollSetting = unitPayrollSettingMongoRepository.findPayrollPeriodById(unitId, id);
        if(isNull(unitPayrollSetting)){
            exceptionService.actionNotPermittedException("");
        }
        UnitPayrollSettingDTO unitPayrollSettingDTO = ObjectMapperUtils.copyPropertiesByMapper(unitPayrollSettingMongoRepository.findPayrollPeriodById(unitId, unitPayrollSetting.getParentPayrollId()), UnitPayrollSettingDTO.class);
        unitPayrollSettingMongoRepository.removeDraftpayrollPeriod(unitId, unitPayrollSetting.getId());
        return unitPayrollSettingDTO;


    }

    public UnitPayrollSettingDTO updatePayrollPeriod(UnitPayrollSettingDTO unitPayrollSettingDTO, Long unitId) {
        UnitPayrollSetting unitPayrollSetting = unitPayrollSettingMongoRepository.findPayrollPeriodById(unitId, unitPayrollSettingDTO.getId());
        if(isNull(unitPayrollSetting)){
            exceptionService.actionNotPermittedException("");
        }
        unitPayrollSetting.setAccessGroupsPriority(ObjectMapperUtils.copyPropertiesOfListByMapper(unitPayrollSettingDTO.getAccessGroupsPriority(), PayrollAccessGroups.class));
        unitPayrollSetting.setPublished(unitPayrollSettingDTO.isPublished());
        if (!unitPayrollSettingDTO.isPublished()) {
            unitPayrollSetting.setPayrollPeriods(ObjectMapperUtils.copyPropertiesOfListByMapper(unitPayrollSettingDTO.getPayrollPeriods(), PayrollPeriod.class));
            unitPayrollSetting.setAccessGroupsPriority(ObjectMapperUtils.copyPropertiesOfListByMapper(unitPayrollSettingDTO.getAccessGroupsPriority(), PayrollAccessGroups.class));
        } else {
            unitPayrollSetting.setPayrollPeriods(validatePayrollPeriod(unitPayrollSettingDTO.getPayrollPeriods(), unitPayrollSetting.getPayrollPeriods()));
            updateParentPayrollPeriod(unitId, unitPayrollSetting.getParentPayrollId(), unitPayrollSetting.getPayrollPeriods());
        }
        unitPayrollSetting.setPublished(unitPayrollSettingDTO.isPublished());
        unitPayrollSettingMongoRepository.save(unitPayrollSetting);
        return ObjectMapperUtils.copyPropertiesByMapper(unitPayrollSetting, UnitPayrollSettingDTO.class);
    }


    public List<UnitPayrollSettingDTO> breakPayrollPeriodOfUnit(Long unitId, UnitPayrollSettingDTO unitPayrollSettingDTO) {
        UnitPayrollSetting unitPayrollSetting = unitPayrollSettingMongoRepository.findPayrollPeriodById(unitId, unitPayrollSettingDTO.getParentPayrollId());
        if(isNull(unitPayrollSetting)){
            exceptionService.actionNotPermittedException("");
        }
        UnitPayrollSetting draftUnitPayrollSetting = unitPayrollSettingMongoRepository.findDraftPayrollPeriodByUnitId(unitId);
        Map<LocalDate, PayrollPeriod> startDateAndPayrollPeriodMap = unitPayrollSetting.getPayrollPeriods().stream().collect(Collectors.toMap(PayrollPeriod::getStartDate, Function.identity()));
        UnitPayrollSetting newUnitPayrollSetting = null;
        if (isNotNull(draftUnitPayrollSetting)) {
            newUnitPayrollSetting = draftUnitPayrollSetting;
            newUnitPayrollSetting = updateOldDraftStateOfPayrollPeriod(unitPayrollSettingDTO, startDateAndPayrollPeriodMap, newUnitPayrollSetting);
        } else {
            newUnitPayrollSetting = getNewDraftStateOfPayroll(unitPayrollSettingDTO, unitPayrollSetting, startDateAndPayrollPeriodMap, newUnitPayrollSetting, unitId);
        }
        unitPayrollSettingMongoRepository.save(newUnitPayrollSetting);
        List<UnitPayrollSettingDTO> unitPayrollSettingDTOS =ObjectMapperUtils.copyPropertiesOfListByMapper(Arrays.asList(newUnitPayrollSetting, unitPayrollSetting), unitPayrollSettingDTO.getClass());
        return getFilterPayrollSettingData(unitPayrollSettingDTOS);
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

    private void updateParentPayrollPeriod(Long unitId, BigInteger parentPayrollId, List<PayrollPeriod> payrollPeriod) {
        UnitPayrollSetting unitPayrollSetting = unitPayrollSettingMongoRepository.findPayrollPeriodById(unitId, parentPayrollId);
        if (isNotNull(unitPayrollSetting)) {
            Map<LocalDate, PayrollPeriod> startDateAndPayrollPeriodMap = unitPayrollSetting.getPayrollPeriods().stream().collect(Collectors.toMap(PayrollPeriod::getStartDate, Function.identity()));
            for (PayrollPeriod period : payrollPeriod) {
                if (startDateAndPayrollPeriodMap.containsKey(period.getStartDate())) {
                    startDateAndPayrollPeriodMap.remove(period.getStartDate());
                }
            }
            unitPayrollSetting.setPayrollPeriods(startDateAndPayrollPeriodMap.keySet().stream().map(date -> startDateAndPayrollPeriodMap.get(date)).collect(Collectors.toList()));
            unitPayrollSettingMongoRepository.save(unitPayrollSetting);
        }
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
            payrollStartDate = getNextStartDate(payrollStartDate, PayrollFrequency.MONTHLY);
        }
        newUnitPayrollSetting.setPayrollPeriods(payrollPeriods);
        newUnitPayrollSetting.setAccessGroupsPriority(unitPayrollSetting.getAccessGroupsPriority());
        return newUnitPayrollSetting;
    }

    private List<PayrollPeriod> validatePayrollPeriod(List<PayrollPeriodDTO> payrollPeriodDTOS, List<PayrollPeriod> payrollPeriods) {
        Map<LocalDate, PayrollPeriod> startDateAndPayrollPeriodMap = payrollPeriods.stream().collect(Collectors.toMap(PayrollPeriod::getStartDate, Function.identity()));
        for (PayrollPeriodDTO payrollPeriodDTO : payrollPeriodDTOS) {
            if (startDateAndPayrollPeriodMap.containsKey(payrollPeriodDTO.getStartDate())) {
                if (isNull(payrollPeriodDTO.getDeadlineDate()) || DateUtils.getLocalDateFromLocalDateTime(payrollPeriodDTO.getDeadlineDate()).isBefore(payrollPeriodDTO.getStartDate())) {
                    exceptionService.actionNotPermittedException("message.payroll.deadline.date.not.invalid", payrollPeriodDTO.getStartDate());
                }
                startDateAndPayrollPeriodMap.get(payrollPeriodDTO.getStartDate()).setDeadlineDate(payrollPeriodDTO.getDeadlineDate());
                int daysTillDeadlineDate = (int) ChronoUnit.DAYS.between(payrollPeriodDTO.getEndDate(), payrollPeriodDTO.getDeadlineDate());
                if (isCollectionNotEmpty(payrollPeriodDTO.getPayrollAccessGroups()) && veryfyGracePeriodOfAccessGroup(payrollPeriodDTO.getPayrollAccessGroups(), daysTillDeadlineDate, payrollPeriodDTO.getStartDate(), payrollPeriodDTO.getEndDate())) {
                    startDateAndPayrollPeriodMap.get(payrollPeriodDTO.getStartDate()).setPayrollAccessGroups(ObjectMapperUtils.copyPropertiesOfListByMapper(payrollPeriodDTO.getPayrollAccessGroups(), PayrollAccessGroups.class));

                }
            }
        }
        return startDateAndPayrollPeriodMap.keySet().stream().map(date -> startDateAndPayrollPeriodMap.get(date)).collect(Collectors.toList());
    }

    private boolean veryfyGracePeriodOfAccessGroup(List<PayrollAccessGroupsDTO> payrollAccessGroupsDTOS, int daysTillDeadlineDate, LocalDate startDate, LocalDate endDate) {
        int sumOfGracePeriod = payrollAccessGroupsDTOS.stream().mapToInt(payrollAccessGroupsDTOs -> payrollAccessGroupsDTOs.getGracePeriod()).sum();
        if (sumOfGracePeriod > daysTillDeadlineDate && isCollectionNotEmpty(payrollAccessGroupsDTOS)) {
            exceptionService.actionNotPermittedException("message.payroll.grace.period.not.invalid", daysTillDeadlineDate, startDate, endDate);
        }
        return true;
    }

    private LocalDate getNextStartDate(LocalDate oldDate, PayrollFrequency payrollFrequency) {
        return PayrollFrequency.MONTHLY.equals(payrollFrequency) ? oldDate.plusMonths(1) : oldDate.plusWeeks(2);
    }

    private boolean validateStartDateForpayrollPeriodCreation(LocalDate startDate, PayrollFrequency payrollFrequency) {
        if (payrollFrequency.equals(PayrollFrequency.FORTNIGHTLY)) {
            return startDate.getDayOfWeek().equals(DayOfWeek.MONDAY);
        } else {
            return startDate.equals(startDate.withDayOfMonth(1));
        }
    }

    private List<UnitPayrollSettingDTO> getFilterPayrollSettingData(List<UnitPayrollSettingDTO> unitPayrollSettingDTO) {
        Map<BigInteger,UnitPayrollSettingDTO> idAndPayrollSettingDTOMap= unitPayrollSettingDTO.stream().collect(Collectors.toMap(k->k.getId(), v->v));
        UnitPayrollSettingDTO unitPayrollSettingDto = unitPayrollSettingDTO.stream().filter(payrollSetting->!payrollSetting.isPublished()).findFirst().orElse(null);
        if(isNotNull(unitPayrollSettingDto) && idAndPayrollSettingDTOMap.containsKey(unitPayrollSettingDto.getParentPayrollId())){
            List<PayrollPeriodDTO> payrollPeriod=idAndPayrollSettingDTOMap.get(unitPayrollSettingDto.getParentPayrollId()).getPayrollPeriods();
            unitPayrollSettingDto.getPayrollPeriods().forEach(payrollPeriodDTO -> {
                payrollPeriod.removeIf(v->v.getStartDate().isEqual(payrollPeriodDTO.getStartDate()));
            });
            idAndPayrollSettingDTOMap.get(unitPayrollSettingDto.getParentPayrollId()).setPayrollPeriods(payrollPeriod);
        }
        return idAndPayrollSettingDTOMap.keySet().stream().map(date -> idAndPayrollSettingDTOMap.get(date)).collect(Collectors.toList());
    }
}
