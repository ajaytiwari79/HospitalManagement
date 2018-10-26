package com.kairos.persistence.repository.data_inventory.Assessment;


import com.kairos.enums.gdpr.AssessmentStatus;
import com.kairos.persistence.model.data_inventory.assessment.Assessment;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.common.AssessmentBasicResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface AssessmentMongoRepository extends MongoBaseRepository<Assessment,BigInteger>,CustomAssessmentRepository {



    @Query("{deleted:false,organizationId:?0,_id:?1}")
    Assessment findByIdAndNonDeleted(Long unitId,BigInteger assessmentId);

    @Query("{deleted:false,organizationId:?0,_id:?1,assessmentStatus:?2}")
    Assessment findByUnitIdAndIdAndAssessmentStatus(Long unitId, BigInteger assessmentId, AssessmentStatus assessmentStatus);

    @Query("{deleted:false,organizationId:?0,assetId:?1}")
    List<AssessmentBasicResponseDTO> findAllAssessmentLaunchedForAssetByAssetIdAndUnitId(Long unitId, BigInteger assetId);

    @Query("{deleted:false,organizationId:?0,processingActivityId:?1}")
    List<AssessmentBasicResponseDTO> findAllAssessmentLaunchedForProcessingActivityByActivityIdAndUnitId(Long unitId, BigInteger processingActivityId);



}
