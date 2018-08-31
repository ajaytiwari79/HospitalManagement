package com.kairos.persistence.repository.wta;

import com.kairos.activity.wta.version.WTAVersionDTO;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.model.wta.WorkingTimeAgreement;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * @author pradeep
 * @date - 21/4/18
 */

public interface CustomWorkingTimeAgreementMongoRepostory {

    List<WTAQueryResultDTO> getWtaByOrganization(Long organizationId);

    WTAQueryResultDTO getOne(BigInteger wtaId);

    List<WTAQueryResultDTO> getAllWTAByOrganizationTypeId(long organizationId);

    List<WTAQueryResultDTO> getAllWTAByCountryId(long countryId);

    List<WTAQueryResultDTO> getAllWTAByOrganizationSubType(long organizationSubTypeId);

    List<WTAQueryResultDTO> getAllWTABySubType(List<Long> subTypeIds, Long countryId);

    List<WTAQueryResultDTO> getAllWTAWithOrganization(long countryId);

    List<WTAQueryResultDTO> getAllWTAWithWTAId(long countryId, BigInteger wtaId);

    WTAQueryResultDTO getVersionOfWTA(BigInteger wtaId);

    List<WTAQueryResultDTO> getAllWtaOfOrganizationByExpertise(Long unitId, Long expertiseId);

    WorkingTimeAgreement getWtaByNameExcludingCurrent(String wtaName, Long countryId, BigInteger wtaId, Long organizationTypeId, Long subOrganizationTypeId);

    boolean checkUniqueWTANameInOrganization(String name, Long unitId, BigInteger wtaId);

    List<WTAQueryResultDTO> getAllWTAByUpIds(List<Long> upIds, Date date);
    List<WTAQueryResultDTO> getAllParentWTAByIds(List<Long> upIds);

    List<WTAQueryResultDTO> getWTAWithVersionIds(List<Long> upIds);

    WTAQueryResultDTO getWTAByUnitPosition(Long unitPositionId,Date date);

}
