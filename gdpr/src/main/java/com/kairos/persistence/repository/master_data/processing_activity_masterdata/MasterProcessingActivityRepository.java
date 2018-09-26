package com.kairos.persistence.repository.master_data.processing_activity_masterdata;

import com.kairos.persistence.model.master_data.default_proc_activity_setting.MasterProcessingActivity;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface MasterProcessingActivityRepository extends MongoBaseRepository<MasterProcessingActivity, BigInteger>, CustomMasterProcessingActivity {


    @Query("{deleted:false,countryId:?0,_id:?1}")
    MasterProcessingActivity findByIdAndCountryId(Long countryId, BigInteger id);

    MasterProcessingActivity findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0,name:?1}")
    MasterProcessingActivity findByNameAndCountryId(Long countryId, String name);

    @Query("{deleted:false,countryId:?0,_id:{$in:?1},subProcess:true}")
    List<MasterProcessingActivity> findAllMasterSubProcessingActivityByIds(Long countryId, Set<BigInteger> subProcessingActivityIds);

    @Query("{deleted:false,countryId:?0,_id:{$in:?1},subProcess:false}")
    List<MasterProcessingActivity> findAllMasterProcessingActivityByIds(Long countryId, Set<BigInteger> subProcessingActivityIds);

}
