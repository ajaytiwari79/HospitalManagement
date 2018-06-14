package com.kairos.persistance.repository.master_data_management.asset_management;


import com.kairos.persistance.model.master_data_management.asset_management.DataDisposal;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
public interface DataDisposalMongoRepository extends MongoRepository<DataDisposal,BigInteger> {


    @Query("{deleted:false,countryId:?0,_id:?1}")
    DataDisposal findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{countryId:?0,name:?1,deleted:false}")
    DataDisposal findByName(Long countryId,String name);

    DataDisposal findByid(BigInteger id);


    @Query("{deleted:false,countryId:?0}")
    List<DataDisposal> findAllDataDisposals(Long countryId);

    @Query("{countryId:?0,name:{$in:?1},deleted:false}")
    List<DataDisposal>  findByCountryAndNameList(Long countryId,Set<String> name);
}
