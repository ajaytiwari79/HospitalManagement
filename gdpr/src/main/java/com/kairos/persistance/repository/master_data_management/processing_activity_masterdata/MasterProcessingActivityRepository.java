package com.kairos.persistance.repository.master_data_management.processing_activity_masterdata;

import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.MasterProcessingActivity;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface MasterProcessingActivityRepository extends MongoRepository<MasterProcessingActivity,BigInteger>,CustomMasterProcessingActivity {


    @Query("{deleted:false,countryId:?0,organizationId:?1,_id:?2}")
    MasterProcessingActivity findByIdAndCountryIdAndNonDeleted(Long countryId,Long organizationId,BigInteger id);

    @Query("{deleted:false,countryId:?0,organizationId:?1}")
    List<MasterProcessingActivity> getAllMasterProcessingsctivity(Long countryId,Long organizationId);

    @Query("{deleted:false,countryId:?0,organizationId:?1,name:{$in:?2}}")
    List<MasterProcessingActivity>  masterProcessingActivityListByNames(Long countryId,Long organizationId,List<String> names);

    MasterProcessingActivity findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0,organizationId:?1,name:?2}")
    MasterProcessingActivity findByNameAndCountryId(Long countryId,Long organizationId,String name);


}
