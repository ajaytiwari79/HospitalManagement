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

    List<WTAResponseDTO> getWtaByOrganization(Long organizationId);
    WTAQueryResultDTO getOne(BigInteger wtaId);

    List<WTAResponseDTO> getAllWTAByOrganizationTypeId(long organizationId);

    List<WTAResponseDTO> getAllWTAByCountryId(long countryId);

    List<WTAResponseDTO> getAllWTAByOrganizationSubType(long organizationSubTypeId);

    List<WTAResponseDTO> getAllWTAWithOrganization(long countryId);

    List<WTAResponseDTO> getAllWTAWithWTAId(long countryId, BigInteger wtaId);

    /*WTAQueryResultDTO getWTAByCountryId(long countryId, BigInteger wtaId);*/

    WTAResponseDTO getVersionOfWTA(BigInteger wtaId);
    List<WTAResponseDTO> getAllWtaOfOrganizationByExpertise(Long unitId,Long expertiseId);

    WorkingTimeAgreement getWtaByNameExcludingCurrent(String wtaName, Long countryId, BigInteger wtaId, Long organizationTypeId, Long subOrganizationTypeId);
}
