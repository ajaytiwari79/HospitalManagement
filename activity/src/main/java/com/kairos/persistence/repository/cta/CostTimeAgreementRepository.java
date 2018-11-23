package com.kairos.persistence.repository.cta;

import com.kairos.dto.activity.cta.CTAResponseDTO;
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



    @Query("{countryId:?0,_id:?1,deleted:false,disabled:false}")
    CostTimeAgreement findCTAByCountryAndIdAndDeleted(Long countryId, BigInteger ctaId, Boolean deleted);

    @Query("{countryId:?0,deleted:false,disabled:false}")
    List<CTAResponseDTO> findCTAByCountryId(Long countryId);

    @Query(value = "{'organization._id':?0, name:?1, deleted:false,disabled:false}",exists = true)
    Boolean isCTAExistWithSameNameInOrganization(Long organizationId, String name);

    @Query(value = "{countryId:?0,name:?1,deleted:false,disabled:false}",exists = true)
    Boolean isCTAExistWithSameNameInCountry(Long countryId, String name);

    @Query(value = "{countryId:?0,name:?1,_id:{$ne:?2},deleted:false,disabled:false}",exists = true)
    Boolean isCTAExistWithSameNameInCountry(Long countryId, String name, BigInteger ctaId);

    @Query(value = "{name:?2,deleted:false,disabled:false,'organizationType._id':?0,'organizationSubType._id':?1}",exists = true)

    Boolean isCTAExistWithSameOrgTypeAndSubType(Long orgType,Long orgSubType, String name);

    @Query(value = "{name:?1,deleted:false,disabled:false,'organization._id':{$in:?0}}")
    List<CostTimeAgreement> findCTAByUnitIdAndOrgTypeAndName(List<Long> organizationIds,String name);



}
