package com.kairos.persistence.repository.master_data.asset_management.data_disposal;


import com.kairos.persistence.model.master_data.default_asset_setting.DataDisposal;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.common.DataDisposalResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
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

    @Query("{deleted:false,countryId:?0,_id:{$in:?1}}")
    List<DataDisposal> getDataDisposalListByIds(Long countryId,Set<BigInteger> dataDisposalIds);

    @Query("{deleted:false,countryId:?0}")
    List<DataDisposalResponseDTO> findAllDataDisposals(Long countryId);

    @Query("{deleted:false,organizationId:?0}")
    List<DataDisposalResponseDTO> findAllOrganizationDataDisposals(Long organizationId);

    @Query("{deleted:false,organizationId:?0,_id:?1}")
    DataDisposal findByOrganizationIdAndId(Long organizationId,BigInteger id);

    @Query("{organizationId:?0,name:?1,deleted:false}")
    DataDisposal findByOrganizationIdAndName(Long organizationId,String name);



}
