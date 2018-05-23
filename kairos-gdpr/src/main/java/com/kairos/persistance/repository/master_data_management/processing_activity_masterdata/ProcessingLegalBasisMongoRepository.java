package com.kairos.persistance.repository.master_data_management.processing_activity_masterdata;

import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.ProcessingLegalBasis;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.List;

public interface ProcessingLegalBasisMongoRepository extends MongoRepository<ProcessingLegalBasis,BigInteger> {

@Query("{'_id':?0,deleted:false}")
ProcessingLegalBasis findByIdAndNonDeleted(BigInteger id);

@Query("{'name':?0,deleted:false}")
ProcessingLegalBasis findByName(String name);

@Query("{'_id':{$in:?0}}")
List<ProcessingLegalBasis> getProcessingLegalBasisList(List<BigInteger> ids);



@Query("{deleted:false}")
    List<ProcessingLegalBasis> findAllProcessingLegalBases();
        }
