package com.kairos.service.counter;
/*
 *Created By Pavan on 29/4/19
 *
 */

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.activity.counter.data.FilterCriteria;
import com.kairos.dto.activity.counter.data.FilterCriteriaDTO;
import com.kairos.dto.activity.counter.distribution.access_group.AccessGroupPermissionCounterDTO;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.enums.KPISetType;
import com.kairos.dto.activity.counter.kpi_set.KPISetDTO;
import com.kairos.dto.activity.kpi.KPIResponseDTO;
import com.kairos.dto.activity.kpi.KPISetResponseDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.DurationType;
import com.kairos.enums.FilterType;
import com.kairos.enums.kpi.KPIRepresentation;
import com.kairos.enums.wta.IntervalUnit;
import com.kairos.persistence.model.counter.ApplicableKPI;
import com.kairos.persistence.model.counter.KPISet;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.persistence.repository.counter.KPISetRepository;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.phase.PhaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;

@Service
public class KPISetService {
    @Inject
    private KPISetRepository kpiSetRepository;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private FibonacciKPIService fibonacciKPIService;
    @Inject
    private CounterRepository counterRepository;
    @Inject
    private PhaseMongoRepository phaseMongoRepository;
    @Inject
    private PhaseService phaseService;
    @Inject
    private CounterDataService counterDataService;

    private static final Logger LOGGER = LoggerFactory.getLogger(KPISetService.class);

    public KPISetDTO createKPISet(Long referenceId, KPISetDTO kpiSetDTO, ConfLevel confLevel) {
        verifyDetails(referenceId, confLevel, kpiSetDTO);
        kpiSetDTO.setReferenceId(referenceId);
        kpiSetDTO.setConfLevel(confLevel);
        KPISet kpiSet = ObjectMapperUtils.copyPropertiesByMapper(kpiSetDTO, KPISet.class);
        kpiSetRepository.save(kpiSet);
        kpiSetDTO.setId(kpiSet.getId());
        return kpiSetDTO;
    }

    public KPISetDTO updateKPISet(Long referenceId, KPISetDTO kpiSetDTO, ConfLevel confLevel) {
        KPISet kpiSet = kpiSetRepository.findOne(kpiSetDTO.getId());
        if (ObjectUtils.isNull(kpiSet)) {
            exceptionService.dataNotFoundByIdException(ActivityMessagesConstants.MESSAGE_DATANOTFOUND, "KPISet", kpiSetDTO.getId());
        }
        verifyDetails(referenceId, confLevel, kpiSetDTO);
        kpiSetDTO.setReferenceId(referenceId);
        kpiSetDTO.setConfLevel(confLevel);
        kpiSet = ObjectMapperUtils.copyPropertiesByMapper(kpiSetDTO, KPISet.class);
        kpiSetRepository.save(kpiSet);
        return kpiSetDTO;
    }

    public boolean deleteKPISet(BigInteger kpiSetId) {
        KPISet kpiSet = kpiSetRepository.findOne(kpiSetId);
        if (ObjectUtils.isNull(kpiSet)) {
            exceptionService.dataNotFoundByIdException(ActivityMessagesConstants.MESSAGE_DATANOTFOUND, "KPISet", kpiSetId);
            return false;
        }
        kpiSet.setDeleted(true);
        kpiSetRepository.save(kpiSet);
        return true;
    }


    public List<KPISetDTO> getAllKPISetByReferenceId(Long referenceId) {
        return kpiSetRepository.findAllByReferenceIdAndDeletedFalse(referenceId);
    }

    public KPISetDTO findById(BigInteger kpiSetId) {
        return kpiSetRepository.findOneById(kpiSetId);
    }

    private void verifyDetails(Long referenceId, ConfLevel confLevel, KPISetDTO kpiSetDTO) {
        validateForVerticalCounter(kpiSetDTO);
        validateConfLevel(referenceId, confLevel);
        boolean existByName = kpiSetRepository.existsByNameIgnoreCaseAndDeletedFalseAndReferenceIdAndIdNot(kpiSetDTO.getName().trim(), referenceId, kpiSetDTO.getId());
        if (existByName) {
            exceptionService.duplicateDataException("message.kpi_set.name.duplicate");
        }
        if (KPISetType.VERTICAL.equals(kpiSetDTO.getKpiSetType())) {
            boolean existsByPhaseAndTimeType = kpiSetRepository.existsByPhaseIdAndTimeTypeAndDeletedFalseAndIdNot(kpiSetDTO.getPhaseId(), kpiSetDTO.getTimeType(), kpiSetDTO.getId());
            if (existsByPhaseAndTimeType) {
                exceptionService.duplicateDataException("message.kpi_set.exist.phase_and_time_type");
            }
        }
        List<ApplicableKPI> applicableKPIs = counterRepository.getApplicableKPIByReferenceId(new ArrayList<>(kpiSetDTO.getKpiIds()), ObjectUtils.newArrayList(referenceId), confLevel);
        if (KPISetType.VERTICAL.equals(kpiSetDTO.getKpiSetType()) && applicableKPIs.stream().noneMatch(applicableKPI -> KPIRepresentation.INDIVIDUAL_STAFF.equals(applicableKPI.getKpiRepresentation()))) {
            exceptionService.actionNotPermittedException("message.kpi_set.belongs_to.individual");
        } else if (KPISetType.HORIZONTAL.equals(kpiSetDTO.getKpiSetType()) && applicableKPIs.stream().anyMatch(applicableKPI -> KPIRepresentation.INDIVIDUAL_STAFF.equals(applicableKPI.getKpiRepresentation()))) {
            exceptionService.actionNotPermittedException("message.kpi_set.belongs_to.unit");
        }

    }

    private void validateConfLevel(Long referenceId, ConfLevel confLevel) {
        if (confLevel.equals(ConfLevel.COUNTRY) && !userIntegrationService.isCountryExists(referenceId)) {
            exceptionService.dataNotFoundByIdException(ActivityMessagesConstants.MESSAGE_COUNTRY_ID);
        } else if (confLevel.equals(ConfLevel.UNIT) && !userIntegrationService.isExistOrganization(referenceId)) {
            exceptionService.dataNotFoundByIdException(ActivityMessagesConstants.MESSAGE_ORGANIZATION_ID);
        }
    }

    private void validateForVerticalCounter(KPISetDTO kpiSetDTO) {
        if(KPISetType.VERTICAL.equals(kpiSetDTO.getKpiSetType())) {
            if (ObjectUtils.isNull(kpiSetDTO.getTimeType())) {
                exceptionService.dataNotFoundByIdException("message.time_type.absent");
            }
            if (ObjectUtils.isNull(kpiSetDTO.getPhaseId())) {
                exceptionService.dataNotFoundByIdException("message.phase.absent");
            }
        }
    }

    public void copyKPISets(Long unitId, List<Long> orgSubTypeIds, Long countryId) {
        List<KPISet> kpiSets = kpiSetRepository.findAllByCountryIdAndDeletedFalse(orgSubTypeIds, countryId);
        List<Phase> unitPhaseList = phaseMongoRepository.findByOrganizationIdAndDeletedFalse(unitId);
        Map<BigInteger, Phase> unitPhaseMap = unitPhaseList.stream().collect(Collectors.toMap(Phase::getParentCountryPhaseId, Function.identity()));
        List<KPISet> unitKPISets = new ArrayList<>();
        kpiSets.forEach(kpiSet -> {
            if(ObjectUtils.isCollectionNotEmpty(kpiSet.getKpiIds())) {
                unitKPISets.add(new KPISet(null,kpiSet.getName(),unitPhaseMap.containsKey(kpiSet.getPhaseId())?unitPhaseMap.get(kpiSet.getPhaseId()).getId():null,unitId,ConfLevel.UNIT,kpiSet.getTimeType(),kpiSet.getKpiIds(),kpiSet.getShortName()));
            }
        });
        if (ObjectUtils.isCollectionNotEmpty(unitKPISets)) {
            kpiSetRepository.saveEntities(unitKPISets);
        }
    }


    public List<KPISetResponseDTO> getKPISetCalculationData(Long unitId, LocalDate startDate, LocalDate endDate) {
        List<KPISetResponseDTO> kpiSetResponseDTOList = new ArrayList<>();
        AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO = userIntegrationService.getAccessGroupIdsAndCountryAdmin(UserContext.getUserDetails().getLastSelectedOrganizationId());
        Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(unitId, DateUtils.asDate(startDate), DateUtils.asDate(startDate.atTime(LocalTime.MAX)));
        if (ObjectUtils.isNotNull(phase)) {
            List<KPISetDTO> kpiSetDTOList = kpiSetRepository.findByPhaseIdAndReferenceIdAndConfLevel(phase.getId(),unitId, ConfLevel.UNIT);
            if (ObjectUtils.isCollectionNotEmpty(kpiSetDTOList)) {
                for (KPISetDTO kpiSet : kpiSetDTOList) {
                    getKPISetResponse(unitId, startDate, endDate, kpiSetResponseDTOList, accessGroupPermissionCounterDTO, kpiSet);
                }
            }
        }
        return kpiSetResponseDTOList;
    }

    private void getKPISetResponse(Long unitId, LocalDate startDate, LocalDate endDate, List<KPISetResponseDTO> kpiSetResponseDTOList, AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO, KPISetDTO kpiSet) {
        KPISetResponseDTO kpiSetResponseDTO = new KPISetResponseDTO();
        kpiSetResponseDTO.setKpiSetType(kpiSet.getKpiSetType());
        kpiSetResponseDTO.setShortName(kpiSet.getShortName());
        List<KPIResponseDTO> kpiResponseDTOList = new ArrayList<>();
        Map<BigInteger, KPIResponseDTO> kpiResponseDTOMap = new HashMap<>();
        if (ObjectUtils.isCollectionNotEmpty(kpiSet.getKpiIds())) {
            kpiSetResponseDTO.setKpiSetName(kpiSet.getName());
            kpiSetResponseDTO.setKpiSetId(kpiSet.getId());
            kpiResponseDTOList = getKPISetCalculation(unitId, startDate, endDate, accessGroupPermissionCounterDTO, kpiSet, kpiResponseDTOMap);
        }
        if (ObjectUtils.isCollectionNotEmpty(kpiResponseDTOList)) {
            kpiSetResponseDTO.setKpiData(kpiResponseDTOList);
        }
        if (ObjectUtils.isCollectionNotEmpty(kpiSetResponseDTO.getKpiData())) {
            kpiSetResponseDTOList.add(kpiSetResponseDTO);
        }
    }

    private List<ApplicableKPI> getApplicableKPIS(AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO, KPISetDTO kpiSet,Long unitId) {
        List<ApplicableKPI> applicableKPIS;
        List<BigInteger> kpiIds = kpiSet.getKpiIds().stream().collect(Collectors.toList());
        if (accessGroupPermissionCounterDTO.isCountryAdmin()) {
            applicableKPIS = counterRepository.getApplicableKPI(kpiIds, ConfLevel.UNIT, unitId);
        } else {
            applicableKPIS = counterRepository.getApplicableKPI(kpiIds, ConfLevel.STAFF, accessGroupPermissionCounterDTO.getStaffId());
        }
        return applicableKPIS;
    }

    private List<KPIResponseDTO> getKPISetCalculation(Long unitId, LocalDate startDate, LocalDate endDate, AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO, KPISetDTO kpiSet, Map<BigInteger, KPIResponseDTO> kpiResponseDTOMap) {
        List<ApplicableKPI> applicableKPIS = getApplicableKPIS(accessGroupPermissionCounterDTO, kpiSet,unitId);
        KPIResponseDTO kpiResponseDTO=null;
        for (ApplicableKPI applicableKPI : applicableKPIS) {
            try {
                if (ObjectUtils.isNotNull(applicableKPI)) {
                    FilterCriteriaDTO filterCriteriaDTO = null;
                    filterCriteriaDTO = updateFilterCriteriaForSetCalculation(unitId, startDate, endDate, accessGroupPermissionCounterDTO, kpiSet, applicableKPI, filterCriteriaDTO);
                    if (ObjectUtils.isNotNull(filterCriteriaDTO)) {
                        kpiResponseDTO = counterDataService.generateKPISetCalculationData(filterCriteriaDTO, unitId, accessGroupPermissionCounterDTO.getStaffId(),startDate);
                    }
                    if (ObjectUtils.isNotNull(kpiResponseDTO)) {
                        kpiResponseDTO.setKpiName(applicableKPI.getTitle());
                        kpiResponseDTOMap.put(kpiResponseDTO.getKpiId(), kpiResponseDTO);
                    }
                }
            }catch (Exception e){
            LOGGER.error(e.getMessage());
            }
        }
        return kpiResponseDTOMap.values().stream().collect(Collectors.toList());
    }

    private FilterCriteriaDTO updateFilterCriteriaForSetCalculation(Long unitId, LocalDate startDate, LocalDate endDate, AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO, KPISetDTO kpiSet, ApplicableKPI applicableKPI, FilterCriteriaDTO filterCriteriaDTO) {
        if (KPISetType.VERTICAL.equals(kpiSet.getKpiSetType())) {
            filterCriteriaDTO = new FilterCriteriaDTO(accessGroupPermissionCounterDTO.isCountryAdmin(), accessGroupPermissionCounterDTO.getCountryId(), accessGroupPermissionCounterDTO.getStaffId(), Arrays.asList(applicableKPI.getActiveKpiId()), KPIRepresentation.REPRESENT_PER_STAFF, applicableKPI.getApplicableFilter().getCriteriaList(), applicableKPI.getInterval(), applicableKPI.getFrequencyType(), applicableKPI.getValue(), unitId);
            applicableKPI.setKpiRepresentation(KPIRepresentation.REPRESENT_PER_STAFF);
        } else {
            if (ObjectUtils.isNotNull(startDate) && ObjectUtils.isNotNull(endDate)) {
                filterCriteriaDTO = new FilterCriteriaDTO(accessGroupPermissionCounterDTO.isCountryAdmin(), accessGroupPermissionCounterDTO.getCountryId(), accessGroupPermissionCounterDTO.getStaffId(), Arrays.asList(applicableKPI.getActiveKpiId()), KPIRepresentation.REPRESENT_PER_INTERVAL, applicableKPI.getApplicableFilter().getCriteriaList(), IntervalUnit.CURRENT, DurationType.HOURS, applicableKPI.getValue(), unitId);
                filterCriteriaDTO.getFilters().add(new FilterCriteria(null, FilterType.TIME_INTERVAL, Arrays.asList(startDate, endDate)));
                applicableKPI.setKpiRepresentation(KPIRepresentation.REPRESENT_PER_INTERVAL);
                applicableKPI.setFrequencyType(DurationType.HOURS);
            }
        }
        return filterCriteriaDTO;
    }

    public Map<String, TranslationInfo> updateTranslationData(BigInteger kpiSetId, Map<String,TranslationInfo> translations){
        KPISet kpiSet = kpiSetRepository.findOne(kpiSetId);
        kpiSet.setTranslations(translations);
        kpiSetRepository.save(kpiSet);
        return kpiSet.getTranslations();
    }
}
