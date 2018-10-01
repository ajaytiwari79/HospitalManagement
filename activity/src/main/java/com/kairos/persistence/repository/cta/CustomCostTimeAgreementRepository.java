package com.kairos.persistence.repository.cta;

import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.persistence.model.cta.CostTimeAgreement;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * @author pradeep
 * @date - 3/8/18
 */

public interface CustomCostTimeAgreementRepository {

    CTAResponseDTO getOneCtaById(BigInteger ctaId);

    CostTimeAgreement getCTAByIdAndOrganizationSubTypeAndCountryId(Long organizationSubTypeId,Long countryId,BigInteger ctaId);

    List<CTAResponseDTO> getAllCTAByOrganizationSubType(Long countryId,Long organizationSubTypeId);

    List<CTAResponseDTO> findCTAByUnitId(Long unitId);

    Boolean isCTAExistWithSameNameInUnit(Long unitId, String name, BigInteger ctaId);

    List<CTAResponseDTO> getDefaultCTA(Long unitId,Long expertiseId);

    List<CTAResponseDTO> getVersionsCTA(List<Long> upIds);
    List<CTAResponseDTO> getParentCTAByUpIds(List<Long> unitPositionIds);
    List<CTAResponseDTO> getCTAByUpIds(List<Long> unitPositionIds);
    CTAResponseDTO getCTAByUnitPositionId(Long unitPositionId,Date date);

    public List<CTAResponseDTO> getCTAByUnitPositionIds(List<Long> unitPositionIds, Date date);
    CostTimeAgreement getCTABasicByUnitPositionAndDate(Long unitPositionId,Date date);

    }
