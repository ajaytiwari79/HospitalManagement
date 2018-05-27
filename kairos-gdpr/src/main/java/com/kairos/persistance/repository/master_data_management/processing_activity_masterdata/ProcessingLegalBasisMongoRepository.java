package com.kairos.persistance.repository.master_data_management.processing_activity_masterdata;

import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.ProcessingLegalBasis;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.List;

public interface ProcessingLegalBasisMongoRepository extends MongoRepository<ProcessingLegalBasis, BigInteger> {


    @Query("{'countryId':?0,_id:?1,deleted:false}")
    ProcessingLegalBasis findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{countryId:?0,name:?1,deleted:false}")
    ProcessingLegalBasis findByName(Long countryId,String name);
    ProcessingLegalBasis findByid(BigInteger id);

    @Query("{_id:{$in:?0}}")
    List<ProcessingLegalBasis> getProcessingLegalBasisList(List<BigInteger> ids);


    @Query("{countryId:?0,deleted:false}")
    List<ProcessingLegalBasis> findAllProcessingLegalBases(Long countryId);

}
