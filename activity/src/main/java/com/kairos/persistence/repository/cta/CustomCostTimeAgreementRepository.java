package com.kairos.persistence.repository.cta;

import com.kairos.activity.cta.CTAResponseDTO;

import java.util.List;

/**
 * @author pradeep
 * @date - 3/8/18
 */

public interface CustomCostTimeAgreementRepository {
    List<CTAResponseDTO> findCTAByCountryId(Long countryId);

    List<CTAResponseDTO> getAllCTAByOrganizationSubType(Long organizationSubTypeId);
    List<CTAResponseDTO> findCTAByUnitId(Long unitId);
}
