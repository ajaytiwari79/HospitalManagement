package com.kairos.persistance.repository.master_data.data_category_element;

import com.kairos.persistance.model.master_data.data_category_element.DataElement;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.master_data.data_mapping.DataElementBasicResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface DataElementMongoRepository extends MongoBaseRepository<DataElement, BigInteger> {

    DataElement findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0,_id:?1}")
    DataElement findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{deleted:false,countryId:?0,name:{$in:?1}}")
    List<DataElement> findByCountryIdAndNames(Long countryId,List<String> names);

    @Query("{deleted:false,countryId:?0}")
    List<DataElement> getAllDataElement(Long countryId);

    @Query("{deleted:false,countryId:?0,_id:{$in:?1}}")
    List<DataElement> getAllDataElementListByIds(Long countryId,List<BigInteger> ids);


    @Query("{deleted:false,organizationId:?0,_id:{$in:?1}}")
    List<DataElement> findAllDataElementByUnitIdAndIds( Long unitId,List<BigInteger> ids);


    @Query("{deleted:false,organizationId:?0}")
    List<DataElementBasicResponseDTO> getAllDataElementByUnitId(Long unitId);


    @Query("{deleted:false,organizationId:?0,_id:?1}")
    DataElementBasicResponseDTO getDataElementByUnitIdAndId( Long organizationId,BigInteger id);


    @Query("{deleted:false,organizationId:?0,_id:?1}")
    DataElement findByUnitIdAndId( Long organizationId,BigInteger id);

}
