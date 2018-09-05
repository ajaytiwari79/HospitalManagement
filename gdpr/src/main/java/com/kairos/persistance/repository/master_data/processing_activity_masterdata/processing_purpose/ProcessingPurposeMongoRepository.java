package com.kairos.persistance.repository.master_data.processing_activity_masterdata.processing_purpose;

import com.kairos.persistance.model.master_data.default_proc_activity_setting.ProcessingPurpose;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.common.ProcessingPurposeResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

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

    @Query("{_id:{$in:?0},deleted:false}")
    List<ProcessingPurposeResponseDTO> findProcessingPurposeByIds(List<BigInteger> processingPurposeIds);

    @Query("{organizationId:?0,deleted:false}")
    List<ProcessingPurposeResponseDTO> findAllOrganizationProcessingPurposes( Long organizationId);


    @Query("{organizationId:?0,name:?1,deleted:false}")
    ProcessingPurpose findByOrganizationIdAndName(Long organizationId,String name);

    @Query("{organizationId:?0,_id:?1,deleted:false}")
    ProcessingPurpose findByOrganizationIdAndId(Long organizationId,BigInteger id);




}
