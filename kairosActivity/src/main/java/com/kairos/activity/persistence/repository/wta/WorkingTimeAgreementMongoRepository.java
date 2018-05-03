package com.kairos.activity.persistence.repository.wta;


import com.kairos.activity.persistence.model.wta.WorkingTimeAgreement;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.web.wta.WTAResponseDTO;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Created by pawanmandhan on 27/7/17.
 */
@Repository
public interface WorkingTimeAgreementMongoRepository extends MongoBaseRepository<WorkingTimeAgreement, BigInteger>,CustomWorkingTimeAgreementMongoRepostory {

    @Query("{name:?0,countryId:?1,deleted:false}")
    WorkingTimeAgreement getWtaByName(String wtaName, Long countryId);



/*    @Query("{organizationType.id:?0,deleted:false}")
    List<WTAResponseDTO> getAllWTAByOrganizationTypeId(long organizationId);

    @Query("{countryId:?0,deleted:false}")
    List<WTAResponseDTO> getAllWTAByCountryId(long countryId);

    @Query("{organizationSubType.id:?0,deleted:false}")
    List<WTAResponseDTO> getAllWTAByOrganizationSubType(long organizationSubTypeId);

    @Query("{countryId:?0,deleted:false}")
    List<Map<String, Object>> getAllWTAWithOrganization(long countryId);

    @Query("{countryId:?0,id:?1,deleted:false}")
    List<Map<String, Object>> getAllWTAWithWTAId(long countryId, BigInteger wtaId);*/

    @Query("{countryId:?0,id:?1,deleted:false}")
    WorkingTimeAgreement getWTAByCountryId(long countryId, BigInteger wtaId);

  /*  @Query("{}")
    WorkingTimeAgreement checkUniquenessOfData(long orgSubTypeId, long orgTypeId, long expertiseId, long countryId);

    @Query("{}")
    WorkingTimeAgreement checkUniquenessOfDataExcludingCurrent(long orgSubTypeId, long orgTypeId, long expertiseId, long countryId, BigInteger wtaId);
*/
    /*@Query("")
    ExpertiseIdListDTO getAvailableAndFreeExpertise(long countryId, long organizationSubTypeId);
*/




    /*@Query("{organization.id:?0,deleted:false}")
    List<WTAResponseDTO> getWtaByOrganization(Long organizationId);*/


   /* @Query("{}")
    void removeOldWorkingTimeAgreement(BigInteger wtaId, Long organizationId, Long endDateInMillis);
*/
    @Query("{id:?0,deleted:false}")
    WorkingTimeAgreement removeOldParentWTAMapping(BigInteger wtaId);

    @Query("{organization.id:?0,id:?1,deleted:false}")
    WorkingTimeAgreement getOrganizationCopyOfWTA(Long unitId, BigInteger wtaId);



    /*@Query("")
    WTAResponseDTO findWtaByUnitEmploymentPosition(Long unitEmploymentPositionId);

    @Query("")
    WTAResponseDTO findRuleTemplateByWTAId(BigInteger wtaId);*/

}