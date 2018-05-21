package com.kairos.persistance.repository.master_data_management.processing_activity_masterdata;

import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.ProcessingPurpose;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface ProcessingPurposeMongoRepository extends MongoRepository<ProcessingPurpose, BigInteger> {


    @Query("{'_id':?0,deleted:false}")
    ProcessingPurpose findByIdAndNonDeleted(BigInteger id);

    @Query("{'name':?0,deleted:false}")
    ProcessingPurpose findByName(String name);


    @Query("{'_id':{$in:?0},deleted:false}")
    List<ProcessingPurpose> processingPurposeList(List<BigInteger> ids);


    @Query("{deleted:false}")
    List<ProcessingPurpose> findAllProcessingPurposes();


}
