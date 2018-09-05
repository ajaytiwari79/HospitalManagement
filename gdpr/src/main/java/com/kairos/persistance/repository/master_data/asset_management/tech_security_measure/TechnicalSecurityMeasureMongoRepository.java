package com.kairos.persistance.repository.master_data.asset_management.tech_security_measure;

import com.kairos.persistance.model.master_data.default_asset_setting.TechnicalSecurityMeasure;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.common.TechnicalSecurityMeasureResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface TechnicalSecurityMeasureMongoRepository extends MongoBaseRepository<TechnicalSecurityMeasure,BigInteger>,CustomTechnicalSecurityRepository{


    @Query("{countryId:?0,_id:?1,deleted:false}")
    TechnicalSecurityMeasure findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{countryId:?0,name:?1,deleted:false}")
    TechnicalSecurityMeasure findByNameAndCountryId(Long countryId,String name);


    TechnicalSecurityMeasure findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0}")
    List<TechnicalSecurityMeasureResponseDTO> findAllTechnicalSecurityMeasures(Long countryId);

    @Query("{deleted:false,countryId:?0,_id:{$in:?1}}")
    List<TechnicalSecurityMeasure> getTechnicalSecurityMeasureListByIds(Long countryId, Set<BigInteger> techSecurityMeasureIds);

    @Query("{deleted:false,_id:{$in:?0}}")
    List<TechnicalSecurityMeasureResponseDTO> findTechnicalSecurityMeasuresListByIds(List<BigInteger> ids);


    @Query("{deleted:false,organizationId:?0}")
    List<TechnicalSecurityMeasureResponseDTO> findAllOrganizationTechnicalSecurityMeasures(Long organizationId);

    @Query("{organizationId:?0,_id:?1,deleted:false}")
    TechnicalSecurityMeasure findByOrganizationIdAndId(Long organizationId,BigInteger id);


    @Query("{organizationId:?0,name:?1,deleted:false}")
    TechnicalSecurityMeasure findByOrganizationIdAndName(Long organizationId,String name);



}
