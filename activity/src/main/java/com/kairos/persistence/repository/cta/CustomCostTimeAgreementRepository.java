package com.kairos.persistence.repository.cta;

import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.persistence.model.cta.CostTimeAgreement;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author pradeep
 * @date - 3/8/18
 */

public interface CustomCostTimeAgreementRepository {

    CTAResponseDTO getOneCtaById(BigInteger ctaId);

    CostTimeAgreement getCTAByIdAndOrganizationSubTypeAndCountryId(Long organizationSubTypeId, Long countryId, BigInteger ctaId);

    List<CTAResponseDTO> getAllCTAByOrganizationSubType(Long countryId, Long organizationSubTypeId);

    List<CTAResponseDTO> findCTAByUnitId(Long unitId);

    Boolean isCTAExistWithSameNameInUnit(Long unitId, String name, BigInteger ctaId);

    List<CTAResponseDTO> getDefaultCTA(Long unitId, Long expertiseId);

    List<CTAResponseDTO> getVersionsCTA(List<Long> upIds);

    List<CTAResponseDTO> getParentCTAByUpIds(List<Long> unitPositionIds);

    List<CTAResponseDTO> getCTAByUpIds(Set<Long> unitPositionIds);

    CTAResponseDTO getCTAByUnitPositionIdAndDate(Long unitPositionId, Date date);

    List<CTAResponseDTO> getCTAByUnitPositionIds(List<Long> unitPositionIds, Date date);

    List<CTAResponseDTO> getCTAByUnitPositionIdsAndDate(List<Long> unitPositionIds, Date startDate,Date endDate);

    CostTimeAgreement getCTABasicByUnitPositionAndDate(Long unitPositionId, Date date);

    void disableOldCta(BigInteger oldctaId, LocalDate endDate);
    void setEndDateToCTAOfUnitPosition(Long unitPositionId, LocalDate endDate);

    List<CTAResponseDTO> getCTAByUnitPositionIdBetweenDate(Long unitPositionId, Date startDate, Date endDate);

    boolean ctaExistsByUnitPositionIdAndDates(Long unitPositionId,Date startDate,Date endDate);

    boolean ctaExistsByUnitPositionIdAndDatesAndNotEqualToId(BigInteger ctaId,Long unitPositionId,Date startDate,Date endDate);

}
