package com.kairos.service.payroll_setting;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.payroll_setting.PayrollAccessGroupsDTO;
import com.kairos.dto.activity.payroll_setting.PayrollPeriodDTO;
import com.kairos.dto.activity.payroll_setting.UnitPayrollSettingDTO;
import com.kairos.dto.scheduler.scheduler_panel.SchedulerPanelDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.payroll_setting.PayrollFrequency;
import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.JobType;
import com.kairos.persistence.model.payroll_setting.PayrollAccessGroups;
import com.kairos.persistence.model.payroll_setting.PayrollPeriod;
import com.kairos.persistence.model.payroll_setting.UnitPayrollSetting;
import com.kairos.persistence.repository.payroll_setting.UnitPayrollSettingMongoRepository;
import com.kairos.rest_client.RestTemplateResponseEnvelope;
import com.kairos.rest_client.SchedulerServiceRestClient;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;

@Service
public class UnitPayrollSettingService extends MongoBaseService {
    private static final Logger logger = LoggerFactory.getLogger(UnitPayrollSettingService.class);
    @Inject
    private UnitPayrollSettingMongoRepository unitPayrollSettingMongoRepository;

    @Inject
    private ExceptionService exceptionService;
    @Inject
    private SchedulerServiceRestClient schedulerRestClient;

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
            unitPayrollSetting.setPayrollPeriods(validatePayrollPeriod(unitPayrollSettingDTO.getPayrollPeriods(), unitPayrollSetting.getPayrollPeriods(), unitPayrollSetting.getPayrollFrequency()));
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


    //in manual add payroll period we need unitId for update payroll period of unit and with job all unit payroll period update and not need unitId
    public List<UnitPayrollSetting> addPayrollPeriodInUnitViaJobOrManual(PayrollFrequency payrollFrequency, Long unitId) {
        List<UnitPayrollSetting> unitPayrollSettings = unitPayrollSettingMongoRepository.getAllPayrollPeriodSettingOfUnitsByPayrollFrequency(payrollFrequency, unitId);
        logger.info("add payroll period in unit");
        if (isCollectionNotEmpty(unitPayrollSettings)) {
            unitPayrollSettings = addPayrollPeriodInUnits(unitPayrollSettings, payrollFrequency);
            unitPayrollSettingMongoRepository.saveEntities(unitPayrollSettings);
            logger.info("successfully added payroll period in unit");
        }
        return unitPayrollSettings;
    }

    public boolean createJobForAddPayrollPeriod() {
        List<SchedulerPanelDTO> schedulerPanelDTOS = Arrays.asList(new SchedulerPanelDTO(JobType.SYSTEM, JobSubType.ADD_PAYROLL_PERIOD, true, LocalTime.of(01, 00), false));
        logger.info("create job for add payroll period");
        schedulerPanelDTOS = schedulerRestClient.publishRequest(schedulerPanelDTOS, null, true, IntegrationOperation.CREATE, "/scheduler_panel", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<SchedulerPanelDTO>>>() {
        });
        logger.info("job registered of add payroll period");
        return isCollectionNotEmpty(schedulerPanelDTOS);
    }


    //use for add payroll period via job or manually
    private List<UnitPayrollSetting> addPayrollPeriodInUnits(List<UnitPayrollSetting> unitPayrollSettings, PayrollFrequency payrollFrequency) {
        List<UnitPayrollSetting> updateUnitPayrollSettings = new ArrayList<>();
        UnitPayrollSetting newUnitPayrollSetting = null;
        updateUnitPayrollSettings.addAll(unitPayrollSettings);
        for (UnitPayrollSetting unitPayrollSetting : unitPayrollSettings) {
            int daysTillDeadlineDate = getDurationBetweenTwoLocalDates(unitPayrollSetting.getPayrollPeriods().get(unitPayrollSetting.getPayrollPeriods().size() - 1).getEndDate(), unitPayrollSetting.getPayrollPeriods().get(unitPayrollSetting.getPayrollPeriods().size() - 1).getDeadlineDate().toLocalDate(), DurationType.DAYS).intValue();
            LocalDate startDateOfNextPayrollPeriod = unitPayrollSetting.getPayrollPeriods().get(unitPayrollSetting.getPayrollPeriods().size() - 1).getEndDate().plusDays(1);
            LocalDate endDateOfPayrollPeriod = getNextStartDate(startDateOfNextPayrollPeriod, PayrollFrequency.MONTHLY);
            while (startDateOfNextPayrollPeriod.isBefore(endDateOfPayrollPeriod)) {
                PayrollPeriod payrollPeriod = new PayrollPeriod(startDateOfNextPayrollPeriod, getNextStartDate(startDateOfNextPayrollPeriod, payrollFrequency).minusDays(1));
                payrollPeriod.setDeadlineDate(getLocalDateTimeFromLocalDateAndLocalTime(payrollPeriod.getEndDate().plusDays(daysTillDeadlineDate), unitPayrollSetting.getPayrollPeriods().get(unitPayrollSetting.getPayrollPeriods().size() - 1).getDeadlineDate().toLocalTime()));
                payrollPeriod.setPayrollAccessGroups(unitPayrollSetting.getPayrollPeriods().get(unitPayrollSetting.getPayrollPeriods().size() - 1).getPayrollAccessGroups());
                if (startDateOfNextPayrollPeriod.isBefore(getlastDayOfYear(unitPayrollSetting.getPayrollPeriods().get(unitPayrollSetting.getPayrollPeriods().size() - 1).getStartDate()))) {
                    unitPayrollSetting.getPayrollPeriods().add(payrollPeriod);
                } else {
                    if (isNull(newUnitPayrollSetting)) {
                        newUnitPayrollSetting = ObjectMapperUtils.copyPropertiesByMapper(unitPayrollSetting, UnitPayrollSetting.class);
                        newUnitPayrollSetting.setId(null);
                        newUnitPayrollSetting.setPayrollPeriods(new ArrayList<>());
                    }
                    newUnitPayrollSetting.getPayrollPeriods().add(payrollPeriod);
                }
                startDateOfNextPayrollPeriod = getNextStartDate(startDateOfNextPayrollPeriod, payrollFrequency);
            }
        }
        if (isNotNull(newUnitPayrollSetting)) {
            updateUnitPayrollSettings.add(newUnitPayrollSetting);
        }
        logger.info("add or update payroll period");
        return updateUnitPayrollSettings;
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
            newUnitPayrollSetting = getNewDraftStateOfPayroll(unitPayrollSettingDTO, unitPayrollSetting, startDateAndPayrollPeriodMap, unitId);
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
            unitPayrollSetting.setPayrollPeriods(startDateAndPayrollPeriodMap.keySet().stream().map(startDateAndPayrollPeriodMap::get).collect(Collectors.toList()));
        }
        return unitPayrollSetting;
    }


    private UnitPayrollSetting getNewDraftStateOfPayroll(UnitPayrollSettingDTO unitPayrollSettingDTO, UnitPayrollSetting unitPayrollSetting, Map<LocalDate, PayrollPeriod> startDateAndPayrollPeriodMap, Long unitId) {
        List<PayrollPeriod> payrollPeriods = new ArrayList<>();
        UnitPayrollSetting newUnitPayrollSetting = new UnitPayrollSetting();
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

    private List<PayrollPeriod> validatePayrollPeriod(List<PayrollPeriodDTO> payrollPeriodDTOS, List<PayrollPeriod> payrollPeriods, PayrollFrequency payrollFrequency) {
        Map<LocalDate, PayrollPeriod> startDateAndPayrollPeriodMap = payrollPeriods.stream().collect(Collectors.toMap(PayrollPeriod::getStartDate, Function.identity()));
        for (PayrollPeriodDTO payrollPeriodDTO : payrollPeriodDTOS) {
            if (startDateAndPayrollPeriodMap.containsKey(payrollPeriodDTO.getStartDate())) {
                if (isNull(payrollPeriodDTO.getDeadlineDate()) || DateUtils.getLocalDateFromLocalDateTime(payrollPeriodDTO.getDeadlineDate()).isBefore(payrollPeriodDTO.getStartDate()) || DateUtils.getLocalDateFromLocalDateTime(payrollPeriodDTO.getDeadlineDate()).isAfter(getNextStartDate(payrollPeriodDTO.getEndDate(), payrollFrequency))) {
                    exceptionService.actionNotPermittedException("message.payroll.deadline.date.not.invalid", payrollPeriodDTO.getStartDate(), payrollPeriodDTO.getEndDate());
                }
                startDateAndPayrollPeriodMap.get(payrollPeriodDTO.getStartDate()).setDeadlineDate(payrollPeriodDTO.getDeadlineDate());
                int daysTillDeadlineDate = (int) ChronoUnit.DAYS.between(payrollPeriodDTO.getEndDate(), payrollPeriodDTO.getDeadlineDate());
                if (verifyGracePeriodOfAccessGroup(payrollPeriodDTO.getPayrollAccessGroups(), daysTillDeadlineDate, payrollPeriodDTO.getStartDate(), payrollPeriodDTO.getEndDate())) {
                    startDateAndPayrollPeriodMap.get(payrollPeriodDTO.getStartDate()).setPayrollAccessGroups(ObjectMapperUtils.copyPropertiesOfListByMapper(payrollPeriodDTO.getPayrollAccessGroups(), PayrollAccessGroups.class));
                }
            }
        }
        return startDateAndPayrollPeriodMap.keySet().stream().map(startDateAndPayrollPeriodMap::get).collect(Collectors.toList());
    }

    private boolean verifyGracePeriodOfAccessGroup(List<PayrollAccessGroupsDTO> payrollAccessGroupsDTOS, int daysTillDeadlineDate, LocalDate startDate, LocalDate endDate) {
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
        Map<BigInteger, UnitPayrollSettingDTO> idAndPayrollSettingDTOMap = unitPayrollSettingDTO.stream().collect(Collectors.toMap(UnitPayrollSettingDTO::getId, v -> v));
        UnitPayrollSettingDTO unitPayrollSettingDto = unitPayrollSettingDTO.stream().filter(payrollSetting -> !payrollSetting.isPublished()).findFirst().orElse(null);
        if (isNotNull(unitPayrollSettingDto) && idAndPayrollSettingDTOMap.containsKey(unitPayrollSettingDto.getParentPayrollId())) {
            List<PayrollPeriodDTO> payrollPeriod = idAndPayrollSettingDTOMap.get(unitPayrollSettingDto.getParentPayrollId()).getPayrollPeriods();
            unitPayrollSettingDto.getPayrollPeriods().forEach(payrollPeriodDTO -> {
                payrollPeriod.removeIf(v -> v.getStartDate().isEqual(payrollPeriodDTO.getStartDate()));
            });
            idAndPayrollSettingDTOMap.get(unitPayrollSettingDto.getParentPayrollId()).setPayrollPeriods(payrollPeriod);
        }
        return idAndPayrollSettingDTOMap.keySet().stream().map(idAndPayrollSettingDTOMap::get).collect(Collectors.toList());
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
