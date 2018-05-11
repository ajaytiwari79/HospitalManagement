package com.kairos.activity.persistence.repository.wta;

import com.kairos.activity.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.activity.persistence.model.wta.WorkingTimeAgreement;
import com.kairos.response.dto.web.wta.WTAResponseDTO;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

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

    List<WTAQueryResultDTO> getAllWTABySubType(List<Long> subTypeIds,Long countryId);
    List<WTAQueryResultDTO> getAllWTAWithOrganization(long countryId);

    List<WTAQueryResultDTO> getAllWTAWithWTAId(long countryId, BigInteger wtaId);

    /*WTAQueryResultDTO getWTAByCountryId(long countryId, BigInteger wtaId);*/

    WTAQueryResultDTO getVersionOfWTA(BigInteger wtaId);
    List<WTAQueryResultDTO> getAllWtaOfOrganizationByExpertise(Long unitId,Long expertiseId);

    WorkingTimeAgreement getWtaByNameExcludingCurrent(String wtaName, Long countryId, BigInteger wtaId, Long organizationTypeId, Long subOrganizationTypeId);

    boolean checkUniqueWTANameInOrganization(String name, Long unitId, BigInteger wtaId);

    List<WTAQueryResultDTO> getAllWTAByIds(List<BigInteger> wtaIds);
}
