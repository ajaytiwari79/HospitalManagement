package com.kairos.persistence.repository.master_data.data_category_element;


import com.kairos.persistence.model.master_data.data_category_element.DataSubjectMapping;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.master_data.data_mapping.DataSubjectMappingBasicResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface DataSubjectMappingRepository extends MongoBaseRepository<DataSubjectMapping, BigInteger>,CustomDataSubjectMappingRepository{


    DataSubjectMapping findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0,_id:?1}")
    DataSubjectMapping findByIdAndNonDeleted(Long countryId,BigInteger id);


    @Query("{deleted:false,countryId:?0,organizationId:?1,name:?2}")
    DataSubjectMapping findByCountryIdAndName(Long countryId,Long organizationId,String name);


    @Query("{deleted:false,organizationId:?0,_id:?1}")
    DataSubjectMapping findByUnitIdAndId(Long organizationId,BigInteger id);



    @Query("{deleted:false,organizationId:?0,dataCategories:?1}")
    List<DataSubjectMappingBasicResponseDTO> findByUnitDataSubjectLinkWithDataCategory(Long unitId, BigInteger dataCategoryId);





}
