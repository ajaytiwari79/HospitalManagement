package com.kairos.persistance.repository.master_data.asset_management.hosting_provider;

import com.kairos.persistance.model.master_data.default_asset_setting.HostingProvider;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.common.HostingProviderResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface HostingProviderMongoRepository extends MongoBaseRepository<HostingProvider,BigInteger>,CustomHostingProviderRepository{

    @Query("{countryId:?0,_id:?1,deleted:false}")
    HostingProvider findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{deleted:false,countryId:?0,name:?1}")
    HostingProvider findByName(Long countryId,String name);

    @Query("{deleted:false,_id:?0}")
    HostingProviderResponseDTO findHostingProviderById(BigInteger id);

    HostingProvider findByid(BigInteger id);

    @Query("{countryId:?0,deleted:false}")
    List<HostingProvider> findAllHostingProviders(Long countryId);


    @Query("{countryId:?0,,name:{$in:?1},deleted:false}")
    List<HostingProvider>  findByCountryAndNameList(Long countryId,Set<String> name);


    @Query("{organizationId:?0,_id:?1,deleted:false}")
    HostingProvider findByOrganizationIdAndId(Long organizationId,BigInteger id);


    @Query("{deleted:false,organizationId:?0,name:?1}")
    HostingProvider findByOrganizationIdAndName(Long organizationId,String name);


    @Query("{organizationId:?0,deleted:false}")
    List<HostingProviderResponseDTO> findAllOrganizationHostingProviders(Long organizationId);



}
