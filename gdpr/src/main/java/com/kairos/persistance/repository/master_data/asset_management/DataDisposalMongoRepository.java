package com.kairos.persistance.repository.master_data.asset_management;


import com.kairos.persistance.model.master_data.default_asset_setting.DataDisposal;
import com.kairos.response.dto.common.DataDisposalResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface DataDisposalMongoRepository extends MongoRepository<DataDisposal,BigInteger> {


    @Query("{deleted:false,countryId:?0,organizationId:?1,_id:?2}")
    DataDisposal findByIdAndNonDeleted(Long countryId,Long organizationId,BigInteger id);

    @Query("{countryId:?0,organizationId:?1,name:?2,deleted:false}")
    DataDisposal findByName(Long countryId,Long organizationId,String name);

    DataDisposal findByid(BigInteger id);

    @Query("{_id:?0,deleted:false}")
    DataDisposalResponseDTO findDataDisposalByid(BigInteger id);

    @Query("{deleted:false,countryId:?0,organizationId:?1}")
    List<DataDisposal> findAllDataDisposals(Long countryId,Long organizationId);

    @Query("{countryId:?0,organizationId:?1,name:{$in:?2},deleted:false}")
    List<DataDisposal>  findByCountryAndNameList(Long countryId,Long organizationId,Set<String> name);
}
