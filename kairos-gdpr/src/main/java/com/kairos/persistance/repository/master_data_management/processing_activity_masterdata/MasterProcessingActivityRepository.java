package com.kairos.persistance.repository.master_data_management.processing_activity_masterdata;

import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.MasterProcessingActivity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface MasterProcessingActivityRepository extends MongoRepository<MasterProcessingActivity,BigInteger>,CustomMasterProcessingActivity {

    @Query("{'_id':?0,deleted:false}")
    MasterProcessingActivity findByIdAndNonDeleted(BigInteger id);

    @Query("{deleted:false}")
    List<MasterProcessingActivity> getAllMasterProcessingsctivity();

    @Query("{'name':{$in:?0},deleted:false}")
    List<MasterProcessingActivity>  masterProcessingActivityListByNames(List<String> name);

    MasterProcessingActivity findByid(BigInteger id);

    @Query("{'name':?0,deleted:false}")
    MasterProcessingActivity findByName(String name);


}
