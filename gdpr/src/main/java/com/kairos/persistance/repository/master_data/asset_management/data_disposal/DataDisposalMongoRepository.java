package com.kairos.persistance.repository.master_data.asset_management.data_disposal;


import com.kairos.persistance.model.master_data.default_asset_setting.DataDisposal;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
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
public interface DataDisposalMongoRepository extends MongoBaseRepository<DataDisposal,BigInteger>,CustomDataDisposalRepository{


    @Query("{deleted:false,countryId:?0,_id:?1}")
    DataDisposal findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{countryId:?0,name:?1,deleted:false}")
    DataDisposal findByName(Long countryId,String name);

    DataDisposal findByid(BigInteger id);

    @Query("{_id:?0,deleted:false}")
    DataDisposalResponseDTO findDataDisposalByid(BigInteger id);

    @Query("{deleted:false,countryId:?0}")
    List<DataDisposal> findAllDataDisposals(Long countryId);

    @Query("{countryId:?0,name:{$in:?1},deleted:false}")
    List<DataDisposal>  findByCountryAndNameList(Long countryId,Set<String> name);



    @Query("{deleted:false,organizationId:?0}")
    List<DataDisposalResponseDTO> findAllOrganizationDataDisposals(Long organizationId);

    @Query("{deleted:false,organizationId:?0,_id:?1}")
    DataDisposal findByOrganizationIdAndId(Long organizationId,BigInteger id);

    @Query("{organizationId:?0,name:?1,deleted:false}")
    DataDisposal findByOrganizationIdAndName(Long organizationId,String name);



}
