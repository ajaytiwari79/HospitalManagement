package com.kairos.persistance.repository.master_data_management.processing_activity_masterdata;

import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.MasterProcessingActivity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface MasterProcessingActivityRepository extends MongoRepository<MasterProcessingActivity,BigInteger>,CustomMasterProcessingActivity {

    @Query("{'countryId':?0,'_id':?1,deleted:false}")
    MasterProcessingActivity findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{deleted:false}")
    List<MasterProcessingActivity> getAllMasterProcessingsctivity();

    @Query("{'countryId':?0,'name':{$in:?1},deleted:false}")
    List<MasterProcessingActivity>  masterProcessingActivityListByNames(Long countryId,List<String> name);

    MasterProcessingActivity findByid(BigInteger id);

    @Query("{'countryId':?0,'name':?1,deleted:false}")
    MasterProcessingActivity findByName(Long countryId,String name);


}
