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


    @Query("{countryId:?0,organizationId:?1,_id:?2,deleted:false}")
    ProcessingPurpose findByIdAndNonDeleted(Long countryId,Long organizationId,BigInteger id);

    @Query("{'countryId':?0,organizationId:?1,name:?2,deleted:false}")
    ProcessingPurpose findByName(Long countryId,Long organizationId,String name);

    ProcessingPurpose findByid(BigInteger id);


    @Query("{countryId:?0,organizationId:?1,_id:{$in:?2},deleted:false}")
    List<ProcessingPurpose> getProcessingPurposeList(Long countryId,Long organizationId,List<BigInteger> ids);


    @Query("{countryId:?0,organizationId:?1,deleted:false}")
    List<ProcessingPurposeResponseDTO> findAllProcessingPurposes(Long countryId, Long organizationId);


    @Query("{countryId:?0,organizationId:?1,name:{$in:?2},deleted:false}")
    List<ProcessingPurpose>  findByCountryAndNameList(Long countryId,Long organizationId,Set<String> name);



}
