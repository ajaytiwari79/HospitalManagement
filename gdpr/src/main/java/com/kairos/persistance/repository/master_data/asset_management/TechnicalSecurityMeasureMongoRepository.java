package com.kairos.persistance.repository.master_data.asset_management;

import com.kairos.persistance.model.master_data.default_asset_setting.TechnicalSecurityMeasure;
import com.kairos.response.dto.common.TechnicalSecurityMeasureReponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface TechnicalSecurityMeasureMongoRepository extends MongoRepository<TechnicalSecurityMeasure,BigInteger> {


    @Query("{countryId:?0,organizationId:?1,_id:?2,deleted:false}")
    TechnicalSecurityMeasure findByIdAndNonDeleted(Long countryId,Long organizationId,BigInteger id);

    @Query("{countryId:?0,organizationId:?1,name:?2,deleted:false}")
    TechnicalSecurityMeasure findByNameAndCountryId(Long countryId,Long organizationId,String name);


    TechnicalSecurityMeasure findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0,organizationId:?1}")
    List<TechnicalSecurityMeasure> findAllTechnicalSecurityMeasures(Long countryId,Long organizationId);

    @Query("{deleted:false,_id:{$in:?0}}")
    List<TechnicalSecurityMeasureReponseDTO> findTechnicalSecurityMeasuresListByIds(List<BigInteger> ids);


    @Query("{countryId:?0,organizationId:?1,name:{$in:?2},deleted:false}")
    List<TechnicalSecurityMeasure>  findByCountryAndNameList(Long countryId,Long organizationId,Set<String> name);
}
