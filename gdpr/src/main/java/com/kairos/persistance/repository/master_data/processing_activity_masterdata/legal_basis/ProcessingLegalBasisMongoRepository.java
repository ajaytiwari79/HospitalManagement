package com.kairos.persistance.repository.master_data.processing_activity_masterdata.legal_basis;

import com.kairos.persistance.model.master_data.default_proc_activity_setting.ProcessingLegalBasis;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.common.ProcessingLegalBasisResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.List;

@JaversSpringDataAuditable
public interface ProcessingLegalBasisMongoRepository extends MongoBaseRepository<ProcessingLegalBasis, BigInteger>,CustomProcessingLegalBasisRepository {


    @Query("{'countryId':?0,_id:?1,deleted:false}")
    ProcessingLegalBasis findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{countryId:?0,name:?1,deleted:false}")
    ProcessingLegalBasis findByName(Long countryId,String name);


    ProcessingLegalBasis findByid(BigInteger id);

    @Query("{_id:{$in:?0},deleted:false}")
    List<ProcessingLegalBasis> findProcessingLegalBasisByIds(List<BigInteger> legalBasisIds);

    @Query("{deleted:false,countryId:?0}")
    List<ProcessingLegalBasisResponseDTO> findAllProcessingLegalBases(Long countryId);

    @Query("{organizationId:?0,deleted:false}")
    List<ProcessingLegalBasisResponseDTO> findAllOrganizationProcessingLegalBases( Long organizationId);

    @Query("{organizationId:?0,_id:?1,deleted:false}")
    ProcessingLegalBasis findByOrganizationIdAndId(Long organizationId,BigInteger id);


    @Query("{organizationId:?0,name:?1,deleted:false}")
    ProcessingLegalBasis findByNameAndOrganizationId(Long organizationId,String name);





}
