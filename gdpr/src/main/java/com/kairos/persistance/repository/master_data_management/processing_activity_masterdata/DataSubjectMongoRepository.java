package com.kairos.persistance.repository.master_data_management.processing_activity_masterdata;


import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.DataSource;
import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.DataSubject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
public interface DataSubjectMongoRepository extends MongoRepository<DataSubject,BigInteger> {

    @Query("{countryId:?0,organizationId:?1,_id:?2,deleted:false}")
    DataSubject findByIdAndNonDeleted(Long countryId,Long organizationId,BigInteger id);

    @Query("{countryId:?0,organizationId:?1,name:?2,deleted:false}")
    DataSubject findByName(Long countryId,Long organizationId,String name);

    @Query("{countryId:?0,organizationId:?1,_id:{$in:?2}}")
    List<DataSubject> getDataSubjectList(Long countryId,Long organizationId,List<BigInteger> ids);

    DataSubject findByid(BigInteger id);


    @Query("{countryId:?0,organizationId:?1,deleted:false}")
    List<DataSubject> findAllDataSubjects(Long countryId,Long organizationId);

    @Query("{countryId:?0,organizationId:?1,name:{$in:?2},deleted:false}")
    List<DataSubject>  findByCountryAndNameList(Long countryId,Long organizationId,Set<String> name);
}
