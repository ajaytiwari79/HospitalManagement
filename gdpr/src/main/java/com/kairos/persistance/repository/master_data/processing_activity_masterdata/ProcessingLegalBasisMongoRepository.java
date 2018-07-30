package com.kairos.persistance.repository.master_data.processing_activity_masterdata;

import com.kairos.persistance.model.master_data.default_proc_activity_setting.ProcessingLegalBasis;
import com.kairos.response.dto.metadata.ProcessingLegalBasisResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@JaversSpringDataAuditable
public interface ProcessingLegalBasisMongoRepository extends MongoRepository<ProcessingLegalBasis, BigInteger> {


    @Query("{'countryId':?0,organizationId:?1,_id:?2,deleted:false}")
    ProcessingLegalBasis findByIdAndNonDeleted(Long countryId,Long organizationId,BigInteger id);

    @Query("{countryId:?0,organizationId:?1,name:?2,deleted:false}")
    ProcessingLegalBasis findByName(Long countryId,Long organizationId,String name);
    ProcessingLegalBasis findByid(BigInteger id);

    @Query("{_id:{$in:?0}}")
    List<ProcessingLegalBasis> getProcessingLegalBasisList(List<BigInteger> ids);


    @Query("{countryId:?0,organizationId:?1,deleted:false}")
    List<ProcessingLegalBasisResponseDTO> findAllProcessingLegalBases(Long countryId, Long organizationId);

    @Query("{countryId:?0,organizationId:?1,name:{$in:?2},deleted:false}")
    List<ProcessingLegalBasis>  findByCountryAndNameList(Long countryId,Long organizationId,Set<String> name);


}
