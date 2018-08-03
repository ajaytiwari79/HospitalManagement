package com.kairos.persistance.repository.master_data.processing_activity_masterdata;

import com.kairos.persistance.model.master_data.default_proc_activity_setting.ProcessingLegalBasis;
import com.kairos.response.dto.common.ProcessingLegalBasisResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@JaversSpringDataAuditable
public interface ProcessingLegalBasisMongoRepository extends MongoRepository<ProcessingLegalBasis, BigInteger> {


    @Query("{'countryId':?0,_id:?1,deleted:false}")
    ProcessingLegalBasis findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{countryId:?0,name:?1,deleted:false}")
    ProcessingLegalBasis findByName(Long countryId,String name);


    ProcessingLegalBasis findByid(BigInteger id);

    @Query("{_id:{$in:?0}}")
    List<ProcessingLegalBasis> getProcessingLegalBasisList(List<BigInteger> ids);

    @Query("{countryId:?0,deleted:false}")
    List<ProcessingLegalBasisResponseDTO> findAllProcessingLegalBases(Long countryId);

    @Query("{organizationId:?0,deleted:false}")
    List<ProcessingLegalBasisResponseDTO> findAllOrganizationProcessingLegalBases( Long organizationId);

    @Query("{organizationId:?0,_id:?1,deleted:false}")
    ProcessingLegalBasis findByOrganizationIdAndId(Long organizationId,BigInteger id);

    @Query("{organizationId:?0,name:?1,deleted:false}")
    ProcessingLegalBasis findByNameAndOrganizationId(Long organizationId,String name);



}
