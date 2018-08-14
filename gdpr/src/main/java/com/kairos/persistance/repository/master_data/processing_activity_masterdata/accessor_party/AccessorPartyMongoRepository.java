package com.kairos.persistance.repository.master_data.processing_activity_masterdata.accessor_party;

import com.kairos.persistance.model.master_data.default_proc_activity_setting.AccessorParty;

import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.common.AccessorPartyResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;


@Repository
@JaversSpringDataAuditable
public interface AccessorPartyMongoRepository extends MongoBaseRepository<AccessorParty,BigInteger>,CustomAccessorPartyRepository {





    @Query("{countryId:?0,_id:?1,deleted:false}")
    AccessorParty findByIdAndNonDeleted(Long countryId, BigInteger id);

    @Query("{countryId:?0,name:?1,deleted:false}")
    AccessorParty findByName(Long countryId,String name);

    @Query("{_id:{$in:?0}}")
    List<AccessorParty> AccessorPartyList(List<BigInteger> ids);

    AccessorParty findByid(BigInteger id);


    @Query("{countryId:?0,deleted:false}")
    List<AccessorPartyResponseDTO> findAllAccessorParty(Long countryId);

    @Query("{_id:{$in:?0},deleted:false}")
    List<AccessorPartyResponseDTO> findAccessorPartyByIds(List<BigInteger> accessorPartyIds);

    @Query("{organizationId:?0,_id:?1,deleted:false}")
    AccessorParty findOrganizationIdAndIdAndNonDeleted(Long organizationId, BigInteger id);

    @Query("{organizationId:?0,name:?1,deleted:false}")
    AccessorParty findByNameAndOrganizationId(Long organizationId,String name);

    @Query("{organizationId:?0,deleted:false}")
    List<AccessorPartyResponseDTO> findAllOrganizationAccessorParty(Long organizationId);
}
