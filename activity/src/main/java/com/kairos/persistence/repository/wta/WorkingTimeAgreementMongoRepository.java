package com.kairos.persistence.repository.wta;


import com.kairos.persistence.model.wta.WorkingTimeAgreement;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

/**
 * Created by pawanmandhan on 27/7/17.
 */
@Repository
public interface WorkingTimeAgreementMongoRepository extends MongoBaseRepository<WorkingTimeAgreement, BigInteger>, CustomWorkingTimeAgreementMongoRepostory {

    @Query(value = "{name:?0,countryId:?1,deleted:false}",exists = true)
    boolean getWtaByName(String wtaName, Long countryId);

    @Query("{countryId:?0,id:?1,deleted:false}")
    WorkingTimeAgreement getWTAByCountryId(long countryId, BigInteger wtaId);

    @Query("{_id:{$in:?0}, deleted:false}")
    List<WorkingTimeAgreement> findAllByIdsInAndDeletedFalse(Set<BigInteger> ids);

    @Query(value = "{name:?2,deleted:false,disabled:false,'organizationType._id':?0,'organizationSubType._id':?1}",exists = true)
    boolean isWTAExistWithSameOrgTypeAndSubType(Long orgType,Long orgSubType, String name);

    @Query(value = "{name:?1,deleted:false,disabled:false,'organization._id':{$in:?0}}")
    List<WorkingTimeAgreement> findWTAByUnitIdsAndName(List<Long> organizationIds, String name);

    @Query(value = "{'organization._id':?0, name:?1, deleted:false}", exists = true)
    boolean isWTAExistByOrganizationIdAndName(long organizationId, String wtaName);


}