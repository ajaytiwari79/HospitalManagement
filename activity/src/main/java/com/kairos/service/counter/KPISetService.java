package com.kairos.service.counter;
/*
 *Created By Pavan on 29/4/19
 *
 */

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.kpi_set.KPISetDTO;
import com.kairos.persistence.model.counter.KPISet;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.persistence.repository.counter.KPISetRepository;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.phase.PhaseService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNull;

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
        verifyDetails(referenceId, confLevel, kpiSetDTO);
        KPISet kpiSet = ObjectMapperUtils.copyPropertiesByMapper(kpiSetRepository.findOne(kpiSetDTO.getId()), KPISet.class);
        if (isNull(kpiSet)) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "KPISet", kpiSetDTO.getId());
        }
        kpiSetRepository.save(kpiSet);
        return kpiSetDTO;
    }

    public boolean deleteKPISet(BigInteger kpiSetId) {
        KPISet kpiSet = kpiSetRepository.findOne(kpiSetId);
        if (isNull(kpiSet)) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "KPISet", kpiSetId);
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
        if (confLevel.equals(ConfLevel.COUNTRY) && !userIntegrationService.isCountryExists(referenceId)) {
            exceptionService.dataNotFoundByIdException("message.country.id");
        }
        if (confLevel.equals(ConfLevel.UNIT) && !userIntegrationService.isExistOrganization(referenceId)) {
            exceptionService.dataNotFoundByIdException("message.organization.id");
        }
        boolean existByName = kpiSetRepository.existsByNameIgnoreCaseAndDeletedFalseAndReferenceIdAndIdNot(kpiSetDTO.getName().trim(), referenceId, kpiSetDTO.getId());
        if (existByName) {
            exceptionService.duplicateDataException("error.kpi.name.duplicate");
        }
        boolean existsByPhaseAndTimeType = kpiSetRepository.existsByPhaseIdAndTimeTypeAndDeletedFalse(kpiSetDTO.getPhaseId(), kpiSetDTO.getTimeType());
        if (existsByPhaseAndTimeType) {
            exceptionService.duplicateDataException("A set is already exists in this phase of " + kpiSetDTO.getTimeType());
        }
        boolean fibonacciKPIExists = fibonacciKPIService.fibonacciKPIExists(kpiSetDTO.getKpiIds());
        if (fibonacciKPIExists) {
            exceptionService.actionNotPermittedException("You can't add fibonacci KPI");
        }
        boolean kpisBelongsToIndividual=counterRepository.allKPIsBelongsToIndividualType(kpiSetDTO.getKpiIds());
        if(!kpisBelongsToIndividual){
            exceptionService.actionNotPermittedException("All KPI should belongs to Individual Type");
        }

    }

    public void copyKPISets(Long unitId, List<Long> orgSubTypeIds, Long countryId) {
        List<KPISet> kpiSets = kpiSetRepository.findAllByCountryIdAndDeletedFalse(orgSubTypeIds, countryId);
        List<Phase> unitPhaseList=phaseMongoRepository.findByOrganizationIdAndDeletedFalse(unitId);
        Map<BigInteger,Phase> unitPhaseMap=unitPhaseList.stream().collect(Collectors.toMap(Phase::getParentCountryPhaseId,Function.identity()));
        List<KPISet> unitKPISets=new ArrayList<>();
        kpiSets.forEach(kpiSet -> {
            KPISet unitKPISet=new KPISet();
            unitKPISet.setId(null);
            unitKPISet.setName(kpiSet.getName());
            unitKPISet.setPhaseId(unitPhaseMap.get(kpiSet.getPhaseId()).getId());
            unitKPISet.setReferenceId(unitId);
            unitKPISet.setConfLevel(ConfLevel.UNIT);
            unitKPISet.setKpiIds(kpiSet.getKpiIds());
            unitKPISets.add(unitKPISet);
        });
        if(isCollectionNotEmpty(unitKPISets)){
            kpiSetRepository.saveEntities(unitKPISets);
        }
    }
}
