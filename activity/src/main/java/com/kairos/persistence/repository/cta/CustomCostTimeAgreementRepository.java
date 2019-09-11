package com.kairos.persistence.repository.cta;

import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.persistence.model.cta.CostTimeAgreement;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

/**
 * @author pradeep
 * @date - 3/8/18
 */

public interface CustomCostTimeAgreementRepository {

    List<CTAResponseDTO> findCTAByCountryId(Long countryId);

    CTAResponseDTO getOneCtaById(BigInteger ctaId);

    CostTimeAgreement getCTAByIdAndOrganizationSubTypeAndCountryId(Long organizationSubTypeId, Long countryId, BigInteger ctaId);

    List<CTAResponseDTO> getAllCTAByOrganizationSubType(Long countryId, Long organizationSubTypeId);

    List<CTAResponseDTO> findCTAByUnitId(Long unitId);

    Boolean isCTAExistWithSameNameInUnit(Long unitId, String name, BigInteger ctaId);

    List<CTAResponseDTO> getDefaultCTA(Long unitId, Long expertiseId);
    List<CTAResponseDTO> getDefaultCTAOfExpertiseAndDate(Long unitId, Long expertiseId,LocalDate selectedDate);

    List<CTAResponseDTO> getVersionsCTA(List<Long> upIds);

    List<CTAResponseDTO> getParentCTAByUpIds(List<Long> employmentIds);

    List<CTAResponseDTO> getCTAByUpIds(Set<Long> employmentIds);

    CTAResponseDTO getCTAByEmploymentIdAndDate(Long employmentId, Date date);

    List<CTAResponseDTO> getCTAByEmploymentIds(List<Long> employmentIds, Date date);

    List<CTAResponseDTO> getCTAByEmploymentIdsAndDate(List<Long> employmentIds, Date startDate, Date endDate);

    CostTimeAgreement getCTABasicByEmploymentAndDate(Long employmentId, Date date);

    void disableOldCta(BigInteger oldctaId, LocalDate endDate);
    void setEndDateToCTAOfEmployment(Long employmentId, LocalDate endDate);

    List<CTAResponseDTO> getCTAByEmploymentIdBetweenDate(Long employmentId, Date startDate, Date endDate);

    boolean ctaExistsByEmploymentIdAndDatesAndNotEqualToId(BigInteger ctaId, Long employmentId, Date startDate, Date endDate);
    List<CTARuleTemplateDTO> getCTARultemplateByEmploymentId(Long employmentId);
}
