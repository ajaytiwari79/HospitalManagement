package com.kairos.persistance.repository.master_data_management.data_category_element;

import com.kairos.persistance.model.master_data_management.data_category_element.DataElement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
public interface DataElementMognoRepository extends MongoRepository<DataElement, BigInteger> {

    DataElement findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0,_id:?1}")
    DataElement findByIdAndNonDeleted(Long countryId, BigInteger id);

    @Query("{deleted:false,countryId:?0,name:{$in:?1}}")
    List<DataElement> findByCountryIdAndNames(Long countryId, Set<String> names);

    @Query("{deleted:false,countryId:?0}")
    List<DataElement> getAllDataElement(Long countryId);

    @Query("{deleted:false,countryId:?0,_id:{$in:?1}}")
    List<DataElement> getAllDataElementListByIds(Long countryId,List<BigInteger> ids);

}
