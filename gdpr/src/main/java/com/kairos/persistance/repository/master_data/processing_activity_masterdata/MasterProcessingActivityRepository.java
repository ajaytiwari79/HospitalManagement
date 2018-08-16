package com.kairos.persistance.repository.master_data.processing_activity_masterdata;

import com.kairos.persistance.model.master_data.default_proc_activity_setting.MasterProcessingActivity;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface MasterProcessingActivityRepository extends MongoBaseRepository<MasterProcessingActivity, BigInteger>, CustomMasterProcessingActivity {


    @Query("{deleted:false,countryId:?0,organizationId:?1,_id:?2}")
    MasterProcessingActivity findByIdAndCountryIdAndNonDeleted(Long countryId, Long organizationId, BigInteger id);

    @Query("{deleted:false,countryId:?0,organizationId:?1}")
    List<MasterProcessingActivity> getAllMasterProcessingActivity(Long countryId, Long organizationId);

    MasterProcessingActivity findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0,organizationId:?1,name:?2}")
    MasterProcessingActivity findByNameAndCountryId(Long countryId, Long organizationId, String name);

    @Query("{deleted:false,countryId:?0,organizationId:?1,_id:{$in:?2},isSubProcess:true}")
    List<MasterProcessingActivity> getAllMasterSubProcessingActivityByIds(Long countryId, Long organizationId, List<BigInteger> subProcessingActivityIds);

}
