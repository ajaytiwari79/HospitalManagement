package com.kairos.service.counter;
/*
 *Created By Pavan on 29/4/19
 *
 */

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.kpi_set.KPISetDTO;
import com.kairos.persistence.model.counter.KPISet;
import com.kairos.persistence.repository.counter.KPISetRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isNull;

@Service
public class KPISetService {
    @Inject
    private KPISetRepository kpiSetRepository;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private ExceptionService exceptionService;

    public KPISetDTO createKPISet(Long referenceId, KPISetDTO kpiSetDTO,ConfLevel confLevel) {
        verifyUnitOrCountry(referenceId,confLevel,kpiSetDTO);
        kpiSetDTO.setReferenceId(referenceId);
        kpiSetDTO.setConfLevel(confLevel);
        KPISet kpiSet = ObjectMapperUtils.copyPropertiesByMapper(kpiSetDTO, KPISet.class);
        kpiSetRepository.save(kpiSet);
        kpiSetDTO.setId(kpiSet.getId());
        return kpiSetDTO;
    }

    public KPISetDTO updateKPISet(Long referenceId,KPISetDTO kpiSetDTO,ConfLevel confLevel) {
        verifyUnitOrCountry(referenceId,confLevel,kpiSetDTO);
        KPISet  kpiSet = ObjectMapperUtils.copyPropertiesByMapper(kpiSetRepository.findOne(kpiSetDTO.getId()), KPISet.class);
        if(isNull(kpiSet)){
            exceptionService.dataNotFoundByIdException("message.dataNotFound","KPISet",kpiSetDTO.getId());
        }
        kpiSetRepository.save(kpiSet);
        return kpiSetDTO;
    }

    public boolean deleteKPISet(BigInteger kpiSetId) {
        KPISet kpiSet = kpiSetRepository.findOne(kpiSetId);
        if(isNull(kpiSet)){
            exceptionService.dataNotFoundByIdException("message.dataNotFound","KPISet",kpiSetId);
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

    private void verifyUnitOrCountry(Long referenceId,ConfLevel confLevel,KPISetDTO kpiSetDTO){
        if(confLevel.equals(ConfLevel.COUNTRY) && !userIntegrationService.isCountryExists(referenceId)) {
            exceptionService.dataNotFoundByIdException("message.country.id");
        }
        if(confLevel.equals(ConfLevel.UNIT) && !userIntegrationService.isExistOrganization(referenceId)){
            exceptionService.dataNotFoundByIdException("message.organization.id");
        }
        boolean existByName = kpiSetRepository.existsByNameIgnoreCaseAndDeletedFalseAndReferenceIdAndIdNot(kpiSetDTO.getName().trim(),referenceId,kpiSetDTO.getId());
        if(existByName){
            exceptionService.duplicateDataException("error.kpi.name.duplicate");
        }
    }

    public void copyKPISets(Long unitId,List<Long> orgSubTypeIds,Long countryId){
        List<KPISet> kpiSetList=kpiSetRepository.findAllByCountryIdAndDeletedFalse(Arrays.asList(14037l),18712l);
    }
}
