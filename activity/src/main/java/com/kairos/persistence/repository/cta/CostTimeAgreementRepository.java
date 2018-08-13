package com.kairos.persistence.repository.cta;

import com.kairos.activity.cta.CTAResponseDTO;
import com.kairos.activity.cta.CTARuleTemplateDTO;
import com.kairos.persistence.model.cta.CostTimeAgreement;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * @author pradeep
 * @date - 30/7/18
 */
@Repository
public interface CostTimeAgreementRepository extends MongoBaseRepository<CostTimeAgreement,BigInteger>,CustomCostTimeAgreementRepository{



    @Query("{countryId:?0,_id:?1,deleted:false}")
    CostTimeAgreement findCTAByCountryAndIdAndDeleted(Long countryId, BigInteger ctaId, Boolean deleted);

    @Query("{countryId:?0,deleted:false}")
    List<CTAResponseDTO> findCTAByCountryId(Long countryId);



  /*  @Query("")
    List<CostTimeAgreement> getAllCTAByOrganizationSubType(List<Long> organizationSubTypeIds, Boolean deleted);

    @Query("")
    void detachCTAFromOrganization(Long orgId, Long ctaId);

    @Query("")
    void detachAllCTAFromOrganization(Long orgId);

    @Query("")
    Boolean isCTALinkedWithCountry(BigInteger ctaId);

    @Query("")
    CostTimeAgreement fetchChildCTA(BigInteger ctaId);

    @Query("")
    void detachParentCTA(BigInteger ctaId);

    @Query("")
    List<CostTimeAgreement> getListOfOrganizationCTAByParentCountryCTA(BigInteger countryCTAId);*/

    @Query(value = "{countryId:?0,name:?1,deleted:false}",exists = true)
    Boolean isCTAExistWithSameNameInCountry(Long countryId, String name);

    @Query("{countryId:?0,name:?1,_id:{$ne:?2},deleted:false}")
    Boolean isCTAExistWithSameNameInCountry(Long countryId, String name, BigInteger ctaId);

       



   /* @Query("")
    void linkParentCountryCTAToOrganization(BigInteger countryCtaId, BigInteger orgCtaId);

    @Query("")
    CostTimeAgreement getCTAIdByCountryAndName(Long countryId, String ctaName);

    @Query("")
    CostTimeAgreement getCTAByUnitPositionId(Long unitPositionId);

    @Query("")
    void detachOldCTAFromUnitPosition(Long unitPositinId);

    @Query("")
    CostTimeAgreement getLinkedCTAWithUnitPosition(Long unitPositinId);*/

    @Query("{}")
    List<CostTimeAgreement> getCTAsByOrganiationSubTypeIdsIn(List<Long> organizationSubTypeIds, long countryId);

/*    @Query("")
    void linkUnitCTAToOrganization(BigInteger unitCtaId, Long organizationId);*/




    @Query("{}")
    Integer getLastSuffixNumberOfCTAName(String name);


}
