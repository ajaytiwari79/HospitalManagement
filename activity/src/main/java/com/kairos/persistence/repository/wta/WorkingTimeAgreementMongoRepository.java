package com.kairos.persistence.repository.wta;


import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.persistence.model.wta.WorkingTimeAgreement;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by pawanmandhan on 27/7/17.
 */
@Repository
public interface WorkingTimeAgreementMongoRepository extends MongoBaseRepository<WorkingTimeAgreement, BigInteger>, CustomWorkingTimeAgreementMongoRepostory {

    @Query(value = "{name:?0,countryId:?1,deleted:false}",exists = true)
    boolean getWtaByName(String wtaName, Long countryId);

    @Query("{countryId:?0,id:?1,deleted:false}")
    WorkingTimeAgreement getWTAByCountryId(long countryId, BigInteger wtaId);

    @Query(value = "{name:?2,deleted:false,disabled:false,'organizationType._id':?0,'organizationSubType._id':?1}",exists = true)
    boolean isWTAExistWithSameOrgTypeAndSubType(Long orgType,Long orgSubType, String name);

    @Query(value = "{name:?1,deleted:false,disabled:false,'organization._id':{$in:?0}}")
    List<WorkingTimeAgreement> findWTAByUnitIdsAndName(List<Long> organizationIds, String name);

    @Query(value = "{deleted:false,disabled:false,'organization._id':?0}")
    List<WTAResponseDTO> findWTAByUnitId(Long organizationIds);

    @Query(value = "{'organization._id':?0, name:?1, deleted:false}", exists = true)
    boolean isWTAExistByOrganizationIdAndName(long organizationId, String wtaName);

    @Query(value = "{organization:{$exists:true},deleted:false}")
    List<WorkingTimeAgreement> findWTAofOrganization();

    @Query(value = "{employmentId:{$in:?0},deleted:false}")
    List<WorkingTimeAgreement> findWTAOfEmployments(Collection<Long> employmentIds);

}