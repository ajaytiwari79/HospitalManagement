package com.kairos.persistence.repository.wta;

import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.model.wta.WorkingTimeAgreement;
import com.kairos.wrapper.wta.CTAWTADTO;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author pradeep
 * @date - 21/4/18
 */

public interface CustomWorkingTimeAgreementMongoRepostory {

    List<WTAQueryResultDTO> getWtaByOrganization(Long organizationId);

    WTAQueryResultDTO getOne(BigInteger wtaId);

    List<WTAQueryResultDTO> getAllWTAByOrganizationTypeId(long organizationId);

    List<WTAQueryResultDTO> getAllWTAByCountryId(long countryId);

    List<WTAQueryResultDTO> getAllWTAByOrganizationSubTypeIdAndCountryId(long organizationSubTypeId, long countryId);

    List<WTAQueryResultDTO> getAllWTABySubType(List<Long> subTypeIds, Long countryId);

    List<WTAQueryResultDTO> getAllWTAWithOrganization(long countryId);

    List<WTAQueryResultDTO> getAllWTAWithWTAId(long countryId, BigInteger wtaId);

    WTAQueryResultDTO getVersionOfWTA(BigInteger wtaId);

    List<WTAQueryResultDTO> getAllWtaOfOrganizationByExpertise(Long unitId, Long expertiseId,LocalDate selectedDate);

    WorkingTimeAgreement getWtaByNameExcludingCurrent(String wtaName, Long countryId, BigInteger wtaId, Long organizationTypeId, Long subOrganizationTypeId);

    WorkingTimeAgreement checkUniqueWTANameInOrganization(String name, Long unitId, BigInteger wtaId);

    List<WTAQueryResultDTO> getAllWTAByUpIds(Set<Long> upIds, Date date);

    List<WTAQueryResultDTO> getAllParentWTAByIds(List<Long> employmentIds);

    List<WTAQueryResultDTO> getWTAWithVersionIds(List<Long> employmentIds);

    WTAQueryResultDTO getWTAByEmploymentIdAndDate(Long employmentId, Date date);

    List<WTAQueryResultDTO> getWTAByEmploymentIds(List<Long> employmentIds, Date date);

    List<WTAQueryResultDTO> getWTAByEmploymentIdsAndDates(List<Long> employmentIds, Date startDate, Date endDate);

    WorkingTimeAgreement getWTABasicByEmploymentAndDate(Long employmentId, Date date);

    void disableOldWta(BigInteger oldwtaId, LocalDate endDate);

    void setEndDateToWTAOfEmployment(Long employmentId, LocalDate endDate);

    boolean wtaExistsByEmploymentIdAndDatesAndNotEqualToId(BigInteger wtaId, Long employmentId, Date startDate, Date endDate);

    List<WTAQueryResultDTO> getWTAByUnitPositionIdAndDates(Long employmentId, Date startDate, Date endDate);

    List<WTAQueryResultDTO> getWTAByUnitPositionIdAndDatesWithRuleTemplateType(Long employmentId, Date startDate, Date endDate, WTATemplateType templateType);

}
