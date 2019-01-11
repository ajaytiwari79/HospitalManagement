package com.kairos.persistence.repository.master_data.data_category_element;

import com.kairos.persistence.model.master_data.data_category_element.DataElement;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface DataElementMongoRepository extends MongoBaseRepository<DataElement, BigInteger>,CustomDataElementRepository {

    DataElement findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0,_id:?1}")
    DataElement findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{deleted:false,_id:?0}")
    DataElement getByIdAndNonDeleted(BigInteger id);


    @Query("{deleted:false,countryId:?0}")
    List<DataElement> getAllDataElementByCountryId(Long countryId);


    @Query("{deleted:false,organizationId:?0,_id:{$in:?1}}")
    List<DataElement> findByUnitIdAndIds(Long unitId, Set<BigInteger> ids);

    @Query("{deleted:false,countryId:?0,_id:{$in:?1}}")
    List<DataElement> findByCountryIdAndIds(Long countryId, Set<BigInteger> ids);


    @Query("{deleted:false,organizationId:?0}")
    List<DataElement> getAllDataElementByUnitId(Long unitId);


    @Query("{deleted:false,organizationId:?0,_id:?1}")
    DataElement getDataElementByUnitIdAndId(Long organizationId, BigInteger id);


    @Query("{deleted:false,organizationId:?0,_id:?1}")
    DataElement findByUnitIdAndId( Long unitId,BigInteger id);


}
