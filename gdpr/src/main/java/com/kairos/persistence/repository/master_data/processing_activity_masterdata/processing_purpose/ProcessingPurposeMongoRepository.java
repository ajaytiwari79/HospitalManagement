package com.kairos.persistence.repository.master_data.processing_activity_masterdata.processing_purpose;

import com.kairos.persistence.model.master_data.default_proc_activity_setting.ProcessingPurpose;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.common.ProcessingPurposeResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface ProcessingPurposeMongoRepository extends MongoBaseRepository<ProcessingPurpose, BigInteger>,CustomProcessingPurposeRepository {


    @Query("{countryId:?0,_id:?1,deleted:false}")
    ProcessingPurpose findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{'countryId':?0,name:?1,deleted:false}")
    ProcessingPurpose findByName(Long countryId,String name);

    ProcessingPurpose findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0}")
    List<ProcessingPurposeResponseDTO> findAllProcessingPurposes(Long countryId);

    @Query("{deleted:false,countryId:?0,_id:{$in:?1}}")
    List<ProcessingPurpose> getProcessingPurposeListByIds(Long countryId, Set<BigInteger> processingPurposeIds);

    @Query("{_id:{$in:?0},deleted:false}")
    List<ProcessingPurposeResponseDTO> findProcessingPurposeByIds(List<BigInteger> processingPurposeIds);

    @Query("{organizationId:?0,deleted:false}")
    List<ProcessingPurposeResponseDTO> findAllOrganizationProcessingPurposes( Long organizationId);


    @Query("{organizationId:?0,name:?1,deleted:false}")
    ProcessingPurpose findByOrganizationIdAndName(Long organizationId,String name);

    @Query("{organizationId:?0,_id:?1,deleted:false}")
    ProcessingPurpose findByOrganizationIdAndId(Long organizationId,BigInteger id);




}
