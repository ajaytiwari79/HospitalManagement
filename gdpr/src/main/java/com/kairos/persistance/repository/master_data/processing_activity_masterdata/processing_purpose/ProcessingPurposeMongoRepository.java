package com.kairos.persistance.repository.master_data.processing_activity_masterdata.processing_purpose;

import com.kairos.persistance.model.master_data.default_proc_activity_setting.ProcessingPurpose;
import com.kairos.response.dto.common.ProcessingPurposeResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface ProcessingPurposeMongoRepository extends MongoRepository<ProcessingPurpose, BigInteger>,CustomProcessingPurposeRepository {


    @Query("{countryId:?0,_id:?1,deleted:false}")
    ProcessingPurpose findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{'countryId':?0,name:?1,deleted:false}")
    ProcessingPurpose findByName(Long countryId,String name);

    ProcessingPurpose findByid(BigInteger id);


    @Query("{countryId:?0,_id:{$in:?1},deleted:false}")
    List<ProcessingPurpose> getProcessingPurposeList(Long countryId,List<BigInteger> ids);



    @Query("{countryId:?0,deleted:false}")
    List<ProcessingPurposeResponseDTO> findAllProcessingPurposes(Long countryId);

    @Query("{organizationId:?0,deleted:false}")
    List<ProcessingPurposeResponseDTO> findAllOrganizationProcessingPurposes( Long organizationId);


    @Query("{organizationId:?0,name:?1,deleted:false}")
    ProcessingPurpose findByOrganizationIdAndName(Long organizationId,String name);

    @Query("{organizationId:?0,_id:?1,deleted:false}")
    ProcessingPurpose findByOrganizationIdAndId(Long organizationId,BigInteger id);




}
