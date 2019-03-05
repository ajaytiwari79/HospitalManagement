package com.kairos.service.payroll_setting;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.payroll_setting.PayrollAccessGroupsDTO;
import com.kairos.dto.activity.payroll_setting.PayrollPeriodDTO;
import com.kairos.dto.activity.payroll_setting.PayrollSettingDTO;
import com.kairos.enums.DurationType;
import com.kairos.persistence.model.payroll_setting.PayrollAccessGroups;
import com.kairos.persistence.model.payroll_setting.PayrollPeriod;
import com.kairos.persistence.model.payroll_setting.PayrollSetting;
import com.kairos.persistence.repository.payroll_setting.PayrollSettingMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;

@Service
public class PayrollSettingService extends MongoBaseService {

    @Inject
    private PayrollSettingMongoRepository payrollSettingMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    public List<Integer> getLocalDatesOfPayrollPeriod(Long unitId) {
        List<PayrollSetting> payrollSettings = payrollSettingMongoRepository.findAllByunitId(unitId);
        return isCollectionNotEmpty(payrollSettings) ? payrollSettings.stream().distinct().map(payrollSetting -> payrollSetting.getPayrollPeriods().get(0).getStartDate().getYear()).collect(Collectors.toList()) : new ArrayList<>();
    }

    public List<PayrollSettingDTO> getPayrollPeriodByUnitIdAndDateAndDurationType(Long unitId, Integer year, DurationType durationType) {
        return payrollSettingMongoRepository.findAllPayrollPeriodByStartDate(unitId, DateUtils.getFirstDayOfYear(year), durationType);

    }

    public PayrollSettingDTO createPayrollPeriod(PayrollSettingDTO payrollSettingDTO, Long unitId) {
        List<PayrollSetting> payrollSettings = new ArrayList<>();
        if (!validateStartDateForpayrollPeriodCreation(payrollSettingDTO.getStartDate(), payrollSettingDTO.getDurationType())) {
            exceptionService.actionNotPermittedException("error.payroll.period.start.date.invalid");
        }
        if (Optional.ofNullable(payrollSettingMongoRepository.findPayrollPeriodByStartDate(unitId, payrollSettingDTO.getStartDate())).isPresent()) {
            exceptionService.actionNotPermittedException("message.payroll.period.date.alreadyexists");
        }
        List<LocalDate> payrollStartDates = new ArrayList<>();
        payrollStartDates.add(payrollSettingDTO.getStartDate());
        if (payrollSettingDTO.getStartDate().plusYears(1).isAfter(DateUtils.getlastDayOfYear(payrollSettingDTO.getStartDate().getYear()))) {
            payrollStartDates.add(DateUtils.getlastDayOfYear(payrollSettingDTO.getStartDate().getYear()).plusDays(1));
        }
        List<PayrollPeriod> payrollPeriods;
        for (LocalDate payrollStartDate : payrollStartDates) {
            payrollPeriods = new ArrayList<>();
            LocalDate payrollEndDate = (payrollStartDate.isBefore(DateUtils.getlastDayOfYear(payrollSettingDTO.getStartDate().getYear())) ? DateUtils.getlastDayOfYear(payrollSettingDTO.getStartDate().getYear()) : payrollSettingDTO.getStartDate().plusYears(1));
            while (payrollStartDate.isBefore(payrollEndDate)) {
                payrollPeriods.add(new PayrollPeriod(payrollStartDate, getNextStartDate(payrollStartDate, payrollSettingDTO.getDurationType()).minusDays(1)));
                payrollStartDate = getNextStartDate(payrollStartDate, payrollSettingDTO.getDurationType());
            }
            payrollSettings.add(new PayrollSetting(false, unitId, payrollPeriods, payrollSettingDTO.getDurationType()));
        }
        payrollSettingMongoRepository.saveEntities(payrollSettings);
        return ObjectMapperUtils.copyPropertiesByMapper(payrollSettings.get(0), PayrollSettingDTO.class);
    }

    public boolean deleteDraftPayrollPeriod(PayrollSettingDTO payrollSettingDTO, Long unitId) {
        Long delete = payrollSettingMongoRepository.removeDraftpayrollPeriod(unitId, payrollSettingDTO.getId());
        return delete > 0;

    }

    public PayrollSettingDTO updatePayrollPeriod(PayrollSettingDTO payrollSettingDTO, Long unitId) {
        PayrollSetting payrollSetting = payrollSettingMongoRepository.findPayrollPeriodById(unitId, payrollSettingDTO.getId());
        payrollSetting.setAccessGroupsPriority(ObjectMapperUtils.copyPropertiesOfListByMapper(payrollSettingDTO.getAccessGroupsPriority(), PayrollAccessGroups.class));
        payrollSetting.setPublished(payrollSettingDTO.isPublished());
        if (!payrollSettingDTO.isPublished()) {
            payrollSetting.setPayrollPeriods(ObjectMapperUtils.copyPropertiesOfListByMapper(payrollSettingDTO.getPayrollPeriods(), PayrollPeriod.class));
            payrollSetting.setAccessGroupsPriority(ObjectMapperUtils.copyPropertiesOfListByMapper(payrollSettingDTO.getAccessGroupsPriority(), PayrollAccessGroups.class));
        } else {
            payrollSetting.setPayrollPeriods(validatePayrollPeriod(payrollSettingDTO.getPayrollPeriods(), payrollSetting.getPayrollPeriods()));
            updateParentPayrollPeriod(unitId, payrollSetting.getParentPayrollId(), payrollSetting.getPayrollPeriods());
        }
        payrollSettingMongoRepository.save(payrollSetting);
        return payrollSettingDTO;
    }


    public PayrollSettingDTO breakPayrollPeriodOfUnit(Long unitId, PayrollSettingDTO payrollSettingDTO) {
        List<PayrollPeriod> payrollPeriods = new ArrayList<>();
        PayrollSetting payrollSetting = payrollSettingMongoRepository.findPayrollPeriodById(unitId, payrollSettingDTO.getParentPayrollId());
        PayrollSetting draftPayrollSetting = payrollSettingMongoRepository.findDraftPayrollPeriodByUnitId(unitId);
        Map<LocalDate, PayrollPeriod> startDateAndPayrollPeriodMap = payrollSetting.getPayrollPeriods().stream().collect(Collectors.toMap(PayrollPeriod::getStartDate, Function.identity()));
        PayrollSetting newPayrollSetting = null;
        if (isNotNull(draftPayrollSetting)) {
            newPayrollSetting = draftPayrollSetting;
            newPayrollSetting = updateOldDraftStateOfPayrollPeriod(payrollSettingDTO, startDateAndPayrollPeriodMap, newPayrollSetting);
        } else {
            newPayrollSetting = getNewDraftStateOfPayroll(payrollSettingDTO, payrollPeriods, payrollSetting, startDateAndPayrollPeriodMap, newPayrollSetting, unitId);
        }
        payrollSettingMongoRepository.save(newPayrollSetting);
        return ObjectMapperUtils.copyPropertiesByMapper(newPayrollSetting, payrollSettingDTO.getClass());
    }

    private PayrollSetting updateOldDraftStateOfPayrollPeriod(PayrollSettingDTO payrollSettingDTO, Map<LocalDate, PayrollPeriod> startDateAndPayrollPeriodMap, PayrollSetting newPayrollSetting) {
        Map<LocalDate, PayrollPeriod> startDateAndDraftPayrollPeriodMap = newPayrollSetting.getPayrollPeriods().stream().collect(Collectors.toMap(PayrollPeriod::getStartDate, Function.identity()));
        LocalDate payrollStartDate = payrollSettingDTO.getStartDate();
        while (payrollStartDate.isBefore(payrollSettingDTO.getEndDate())) {
            if (startDateAndPayrollPeriodMap.containsKey(payrollStartDate) && !startDateAndDraftPayrollPeriodMap.containsKey(payrollStartDate)) {
                newPayrollSetting.getPayrollPeriods().add(startDateAndPayrollPeriodMap.get(payrollStartDate));
            }
            payrollStartDate = getNextStartDate(payrollStartDate, payrollSettingDTO.getDurationType());
        }
        return newPayrollSetting;
    }

    private void updateParentPayrollPeriod(Long unitId, BigInteger parentPayrollId, List<PayrollPeriod> payrollPeriod) {
        PayrollSetting payrollSetting = payrollSettingMongoRepository.findPayrollPeriodById(unitId, parentPayrollId);
        Map<LocalDate, PayrollPeriod> startDateAndPayrollPeriodMap = payrollSetting.getPayrollPeriods().stream().collect(Collectors.toMap(PayrollPeriod::getStartDate, Function.identity()));
        for (PayrollPeriod period : payrollPeriod) {
            if (startDateAndPayrollPeriodMap.containsKey(period.getStartDate())) {
                startDateAndPayrollPeriodMap.remove(period.getStartDate());
            }
        }
        payrollSetting.setPayrollPeriods(startDateAndPayrollPeriodMap.keySet().stream().map(date -> startDateAndPayrollPeriodMap.get(date)).collect(Collectors.toList()));
        payrollSettingMongoRepository.save(payrollSetting);
    }


    private PayrollSetting getNewDraftStateOfPayroll(PayrollSettingDTO payrollSettingDTO, List<PayrollPeriod> payrollPeriods, PayrollSetting payrollSetting, Map<LocalDate, PayrollPeriod> startDateAndPayrollPeriodMap, PayrollSetting newPayrollSetting, Long unitId) {
        newPayrollSetting = new PayrollSetting();
        newPayrollSetting.setPublished(payrollSettingDTO.isPublished());
        newPayrollSetting.setUnitId(unitId);
        newPayrollSetting.setParentPayrollId(payrollSettingDTO.getParentPayrollId());
        newPayrollSetting.setDurationType(payrollSettingDTO.getDurationType());
        LocalDate payrollStartDate = payrollSettingDTO.getStartDate();
        while (payrollStartDate.isBefore(payrollSettingDTO.getEndDate())) {
            if (startDateAndPayrollPeriodMap.containsKey(payrollStartDate)) {
                payrollPeriods.add(startDateAndPayrollPeriodMap.get(payrollStartDate));
            }
            payrollStartDate = getNextStartDate(payrollStartDate, payrollSettingDTO.getDurationType());
        }
        newPayrollSetting.setPayrollPeriods(payrollPeriods);
        newPayrollSetting.setAccessGroupsPriority(payrollSetting.getAccessGroupsPriority());
        return newPayrollSetting;
    }

    private List<PayrollPeriod> validatePayrollPeriod(List<PayrollPeriodDTO> payrollPeriodDTOS, List<PayrollPeriod> payrollPeriods) {
        Map<LocalDate, PayrollPeriod> startDateAndPayrollPeriodMap = payrollPeriods.stream().collect(Collectors.toMap(PayrollPeriod::getStartDate, Function.identity()));
        for (PayrollPeriodDTO payrollPeriodDTO : payrollPeriodDTOS) {
            if (startDateAndPayrollPeriodMap.containsKey(payrollPeriodDTO.getStartDate())) {
                if (isNotNull(payrollPeriodDTO.getDeadlineDate())) {
                    startDateAndPayrollPeriodMap.get(payrollPeriodDTO.getStartDate()).setDeadlineDate(payrollPeriodDTO.getDeadlineDate());
                    int daysTillDeadlineDate = (int) ChronoUnit.DAYS.between(payrollPeriodDTO.getEndDate(), payrollPeriodDTO.getDeadlineDate());
                    if (isCollectionNotEmpty(payrollPeriodDTO.getPayrollAccessGroups()) && veryfyGracePeriodOfAccessGroup(payrollPeriodDTO.getPayrollAccessGroups(), daysTillDeadlineDate, payrollPeriodDTO.getStartDate())) {
                        startDateAndPayrollPeriodMap.get(payrollPeriodDTO.getStartDate()).setPayrollAccessGroups(ObjectMapperUtils.copyPropertiesOfListByMapper(payrollPeriodDTO.getPayrollAccessGroups(), PayrollAccessGroups.class));
                    }
                }
            }
        }
        return startDateAndPayrollPeriodMap.keySet().stream().map(date -> startDateAndPayrollPeriodMap.get(date)).collect(Collectors.toList());
    }

    private boolean veryfyGracePeriodOfAccessGroup(List<PayrollAccessGroupsDTO> payrollAccessGroupsDTOS, int daysTillDeadlineDate, LocalDate startDate) {
        int sumOfGracePeriod = payrollAccessGroupsDTOS.stream().mapToInt(payrollAccessGroupsDTOs -> payrollAccessGroupsDTOs.getGracePeriod()).sum();
        if (!(sumOfGracePeriod == daysTillDeadlineDate)) {
            exceptionService.actionNotPermittedException("access groups" + " " + startDate);
        }
        return sumOfGracePeriod == daysTillDeadlineDate;
    }

    private LocalDate getNextStartDate(LocalDate oldDate, DurationType durationType) {
        return DurationType.MONTHS.equals(durationType) ? oldDate.plusMonths(1) : oldDate.plusWeeks(2);
    }

    private boolean validateStartDateForpayrollPeriodCreation(LocalDate startDate, DurationType durationType) {
        if (durationType.equals(DurationType.WEEKS)) {
            return startDate.getDayOfWeek().equals(DayOfWeek.MONDAY);
        } else {
            return startDate.equals(startDate.withDayOfMonth(1));
        }
    }

}
