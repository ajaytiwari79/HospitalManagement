package com.kairos.persistance.repository.master_data_management.processing_activity_masterdata;

import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.MasterProcessingActivity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface MasterProcessingActivityRepository extends MongoRepository<MasterProcessingActivity,BigInteger>,CustomMasterProcessingActivity {


    @Query("{deleted:false,countryId:?0,_id:?1}")
    MasterProcessingActivity findByIdAndCountryIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{deleted:false,countryId:?0}")
    List<MasterProcessingActivity> getAllMasterProcessingsctivity(Long countryId);

    @Query("{deleted:false,countryId:?0,name:{$in:?1}}")
    List<MasterProcessingActivity>  masterProcessingActivityListByNames(Long countryId,List<String> names);

    MasterProcessingActivity findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0,name:{$regex:?1,$options:'i'}}")
    MasterProcessingActivity findByNameAndCountryId(Long countryId,String name);


}
