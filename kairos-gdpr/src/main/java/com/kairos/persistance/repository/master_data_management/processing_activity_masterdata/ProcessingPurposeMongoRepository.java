package com.kairos.persistance.repository.master_data_management.processing_activity_masterdata;

import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.ProcessingPurpose;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface ProcessingPurposeMongoRepository extends MongoRepository<ProcessingPurpose, BigInteger> {


    @Query("{'countryId':?0,'_id':?1,deleted:false}")
    ProcessingPurpose findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{'countryId':?0,'name':?1,deleted:false}")
    ProcessingPurpose findByName(Long countryId,String name);

    ProcessingPurpose findByid(BigInteger id);


    @Query("{'countryId':?0,'_id':{$in:?1},deleted:false}")
    List<ProcessingPurpose> getProcessingPurposeList(Long countryId,List<BigInteger> ids);


    @Query("{'countryId':?0,deleted:false}")
    List<ProcessingPurpose> findAllProcessingPurposes(Long countryId);


}
