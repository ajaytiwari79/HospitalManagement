package com.kairos.service.counter;
/*
 *Created By Pavan on 29/4/19
 *
 */

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.counter.kpi_set.KPISetDTO;
import com.kairos.persistence.model.counter.KPISet;
import com.kairos.persistence.repository.counter.KPISetRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

@Service
public class KPISetService {
    @Inject
    private KPISetRepository kpiSetRepository;

    public KPISetDTO createKPISet(Long countryId, KPISetDTO kpiSetDTO) {
        KPISet kpiSet = ObjectMapperUtils.copyPropertiesByMapper(kpiSetDTO, KPISet.class);
        kpiSet.setCountryId(countryId);
        kpiSetRepository.save(kpiSet);
        return kpiSetDTO;
    }

    public KPISetDTO updateKPISet(KPISetDTO kpiSetDTO) {
        KPISet  kpiSet = ObjectMapperUtils.copyPropertiesByMapper(kpiSetRepository.findOne(kpiSetDTO.getId()), KPISet.class);
        kpiSetRepository.save(kpiSet);
        return kpiSetDTO;
    }

    public boolean deleteKPISet(BigInteger kpiSetId) {
        KPISet kpiSet = kpiSetRepository.findOne(kpiSetId);
        kpiSet.setDeleted(true);
        kpiSetRepository.save(kpiSet);
        return true;
    }

    public List<KPISetDTO> getAllKPISetByCountryId(Long countryId) {
        return kpiSetRepository.findAllByCountryIdAndDeletedFalse(countryId);
    }
}
